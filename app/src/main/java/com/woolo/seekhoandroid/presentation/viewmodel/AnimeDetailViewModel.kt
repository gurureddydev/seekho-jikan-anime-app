package com.woolo.seekhoandroid.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.woolo.seekhoandroid.domain.model.AnimeDetail
import com.woolo.seekhoandroid.domain.model.Character
import com.woolo.seekhoandroid.domain.usecase.GetAnimeDetailUseCase
import com.woolo.seekhoandroid.domain.usecase.GetAnimeCharactersUseCase
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
class AnimeDetailViewModel @Inject constructor(
    private val getAnimeDetailUseCase: GetAnimeDetailUseCase,
    private val getAnimeCharactersUseCase: GetAnimeCharactersUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<AnimeDetailUiState>(AnimeDetailUiState())
    val uiState: StateFlow<AnimeDetailUiState> = _uiState.asStateFlow()

    fun loadAnimeDetail(malId: Int) {
        viewModelScope.launch {
            // Load anime detail
            getAnimeDetailUseCase(malId)
                .onEach { result ->
                    when (result) {
                        is Result.Loading -> {
                            // Netflix-style: Only show loading if we have no cached data
                            // If we already have data, keep showing it while refreshing in background
                            if (_uiState.value.animeDetail == null) {
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
                                animeDetail = result.data,
                                error = null
                            )
                            Timber.d("AnimeDetailViewModel: Updated anime detail for malId=$malId")
                            
                            // Load characters after anime detail is loaded
                            loadAnimeCharacters(malId)
                        }
                        is Result.Error -> {
                            // Only show error if we have no cached data to show
                            if (_uiState.value.animeDetail == null) {
                                _uiState.value = _uiState.value.copy(
                                    isLoading = false,
                                    error = result.exception.message ?: "Unknown error occurred"
                                )
                                Timber.e(result.exception, "Error loading anime detail")
                            } else {
                                // If we have cached data, keep showing it and don't show error
                                Timber.w("AnimeDetailViewModel: Network error but showing cached data for malId=$malId")
                                // Still try to load characters even if detail load failed
                                loadAnimeCharacters(malId)
                            }
                        }
                    }
                }
                .launchIn(viewModelScope)
        }
    }

    private fun loadAnimeCharacters(malId: Int) {
        viewModelScope.launch {
            getAnimeCharactersUseCase(malId)
                .onEach { result ->
                    when (result) {
                        is Result.Loading -> {
                            _uiState.value = _uiState.value.copy(
                                isLoadingCharacters = true,
                                charactersError = null
                            )
                        }
                        is Result.Success -> {
                            _uiState.value = _uiState.value.copy(
                                isLoadingCharacters = false,
                                characters = result.data,
                                charactersError = null
                            )
                            Timber.d("AnimeDetailViewModel: Updated characters for malId=$malId (${result.data.size} characters)")
                        }
                        is Result.Error -> {
                            _uiState.value = _uiState.value.copy(
                                isLoadingCharacters = false,
                                charactersError = result.exception.message ?: "Failed to load characters"
                            )
                            Timber.e(result.exception, "Error loading characters for malId=$malId")
                        }
                    }
                }
                .launchIn(viewModelScope)
        }
    }

    fun retry(malId: Int) {
        loadAnimeDetail(malId)
    }
}

data class AnimeDetailUiState(
    val isLoading: Boolean = false,
    val animeDetail: AnimeDetail? = null,
    val error: String? = null,
    val isLoadingCharacters: Boolean = false,
    val characters: List<Character> = emptyList(),
    val charactersError: String? = null
)

