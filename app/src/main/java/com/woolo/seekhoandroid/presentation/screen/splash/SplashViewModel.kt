package com.woolo.seekhoandroid.presentation.screen.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.woolo.seekhoandroid.domain.usecase.GetTopAnimeUseCase
import com.woolo.seekhoandroid.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val getTopAnimeUseCase: GetTopAnimeUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(SplashUiState())
    val uiState: StateFlow<SplashUiState> = _uiState.asStateFlow()
    
    init {
        loadSplashAnime()
    }
    
    private fun loadSplashAnime() {
        viewModelScope.launch {
            val startTime = System.currentTimeMillis()
            val minSplashDuration = 2000L // Minimum 2 seconds
            
            try {
                // Get anime list
                getTopAnimeUseCase()
                    .first { result ->
                        when (result) {
                            is Result.Success -> {
                                // Select a random or top anime for splash
                                val animeList = result.data
                                if (animeList.isNotEmpty()) {
                                    // Prefer top-ranked anime (rank 1-5) for splash
                                    val featuredAnime = animeList
                                        .filter { it.rank != null && it.rank <= 5 }
                                        .randomOrNull() 
                                        ?: animeList.random()
                                    
                                    _uiState.value = _uiState.value.copy(
                                        animeImageUrl = featuredAnime.imageUrl,
                                        isLoading = false
                                    )
                                    Timber.d("SplashViewModel: Selected anime: ${featuredAnime.title}")
                                } else {
                                    _uiState.value = _uiState.value.copy(isLoading = false)
                                }
                                true // Stop collecting
                            }
                            is Result.Loading -> {
                                _uiState.value = _uiState.value.copy(isLoading = true)
                                false // Continue collecting
                            }
                            is Result.Error -> {
                                Timber.e(result.exception, "Error loading splash anime")
                                _uiState.value = _uiState.value.copy(isLoading = false)
                                true // Stop collecting
                            }
                        }
                    }
                
                // Ensure minimum splash screen duration
                val elapsedTime = System.currentTimeMillis() - startTime
                val remainingTime = minSplashDuration - elapsedTime
                if (remainingTime > 0) {
                    delay(remainingTime)
                }
                
                // Navigate to anime list
                _uiState.value = _uiState.value.copy(shouldNavigate = true)
            } catch (e: Exception) {
                Timber.e(e, "Error in splash screen")
                // Still navigate even if there's an error
                val elapsedTime = System.currentTimeMillis() - startTime
                val remainingTime = minSplashDuration - elapsedTime
                if (remainingTime > 0) {
                    delay(remainingTime)
                }
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    shouldNavigate = true
                )
            }
        }
    }
}

data class SplashUiState(
    val isLoading: Boolean = true,
    val animeImageUrl: String? = null,
    val shouldNavigate: Boolean = false
)

