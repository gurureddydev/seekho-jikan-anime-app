package com.woolo.seekhoandroid.presentation.screen.splash

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.size.Size
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onNavigateToAnimeList: () -> Unit,
    viewModel: SplashViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    // Animate logo/app name fade in
    val logoAlpha by animateFloatAsState(
        targetValue = if (uiState.isLoading) 0f else 1f,
        animationSpec = tween(durationMillis = 1000),
        label = "logo_alpha"
    )
    
    // Animate background image fade in
    val backgroundAlpha by animateFloatAsState(
        targetValue = if (uiState.isLoading || uiState.animeImageUrl == null) 0f else 0.6f,
        animationSpec = tween(durationMillis = 1500),
        label = "background_alpha"
    )
    
    // Navigate after splash duration
    LaunchedEffect(uiState.shouldNavigate) {
        if (uiState.shouldNavigate) {
            delay(500) // Small delay for smooth transition
            onNavigateToAnimeList()
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F0F0F))
    ) {
        // Dynamic anime background image
        if (uiState.animeImageUrl != null) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(uiState.animeImageUrl)
                    .size(Size.ORIGINAL)
                    .build(),
                contentDescription = "Splash Background",
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(backgroundAlpha),
                contentScale = ContentScale.Crop
            )
            
            // Gradient overlay for better text readability
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.7f)
                            )
                        )
                    )
            )
        }
        
        // App Logo/Name centered
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.alpha(logoAlpha)
            ) {
                // App Name/Logo
                Text(
                    text = "SEEKHO",
                    style = MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 48.sp,
                    letterSpacing = 4.sp
                )
                
                // Subtitle
                Text(
                    text = "Discover Anime",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 16.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
        
        // Loading indicator (if still loading anime image)
        if (uiState.isLoading && uiState.animeImageUrl == null) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 64.dp)
            ) {
                // Simple loading dots or progress indicator
                androidx.compose.material3.CircularProgressIndicator(
                    modifier = Modifier.size(32.dp),
                    color = Color.White.copy(alpha = 0.6f)
                )
            }
        }
    }
}

