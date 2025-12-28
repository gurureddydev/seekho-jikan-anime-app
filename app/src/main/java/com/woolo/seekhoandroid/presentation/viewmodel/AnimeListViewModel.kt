package com.woolo.seekhoandroid.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.woolo.seekhoandroid.domain.model.Anime
import com.woolo.seekhoandroid.domain.usecase.GetTopAnimeUseCase
import com.woolo.seekhoandroid.domain.usecase.SyncAnimeListUseCase
import com.woolo.seekhoandroid.util.NetworkConnectivityObserver
import com.woolo.seekhoandroid.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class AnimeListViewModel @Inject constructor(
    private val getTopAnimeUseCase: GetTopAnimeUseCase,
    private val syncAnimeListUseCase: SyncAnimeListUseCase,
    private val networkConnectivityObserver: NetworkConnectivityObserver,
    private val syncManager: com.woolo.seekhoandroid.util.SyncManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<AnimeListUiState>(AnimeListUiState())
    val uiState: StateFlow<AnimeListUiState> = _uiState.asStateFlow()

    private val _isOnline = MutableStateFlow(true)
    val isOnline: StateFlow<Boolean> = _isOnline.asStateFlow()

    init {
        observeNetworkConnectivity()
        loadAnimeList()
        // Schedule periodic sync
        syncManager.schedulePeriodicSync()
    }

    private fun observeNetworkConnectivity() {
        networkConnectivityObserver.isOnline
            .onEach { isOnline ->
                _isOnline.value = isOnline
                if (isOnline && _uiState.value.animeList.isEmpty()) {
                    // Auto-sync when coming back online
                    syncAnimeList()
                }
            }
            .launchIn(viewModelScope)
    }

    fun loadAnimeList() {
        viewModelScope.launch {
            getTopAnimeUseCase()
                .onEach { result ->
                    when (result) {
                        is Result.Loading -> {
                            // Netflix-style: Only show loading if we have no cached data
                            // If we already have data, keep showing it while refreshing in background
                            if (_uiState.value.animeList.isEmpty()) {
                                _uiState.value = _uiState.value.copy(
                                    isLoading = true,
                                    error = null
                                )
                            }
                            // If we have cached data, don't change isLoading - keep showing cached data
                        }
                        is Result.Success -> {
                            // Update data (could be cached or fresh)
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                animeList = result.data,
                                error = null
                            )
                            Timber.d("AnimeListViewModel: Updated anime list (${result.data.size} items)")
                        }
                        is Result.Error -> {
                            // Only show error if we have no cached data to show
                            if (_uiState.value.animeList.isEmpty()) {
                                _uiState.value = _uiState.value.copy(
                                    isLoading = false,
                                    error = result.exception.message ?: "Unknown error occurred"
                                )
                                Timber.e(result.exception, "Error loading anime list")
                            } else {
                                // If we have cached data, keep showing it and don't show error
                                Timber.w("AnimeListViewModel: Network error but showing cached data")
                            }
                        }
                    }
                }
                .launchIn(viewModelScope)
        }
    }

    fun retry() {
        loadAnimeList()
    }

    fun syncAnimeList() {
        viewModelScope.launch {
            try {
                syncAnimeListUseCase()
                loadAnimeList()
            } catch (e: Exception) {
                Timber.e(e, "Error syncing anime list")
            }
        }
    }
}

data class AnimeListUiState(
    val isLoading: Boolean = false,
    val animeList: List<Anime> = emptyList(),
    val error: String? = null
)

