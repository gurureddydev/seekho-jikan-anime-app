package com.woolo.seekhoandroid.presentation.screen.anime_list

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.SemiBold,
        color = Color.White,
        modifier = modifier
    )
}

@Composable
fun ErrorContent(
    error: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.background(Color(0xFF0F0F0F)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Error",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = error,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 8.dp),
                color = Color.White.copy(alpha = 0.7f)
            )
            Button(onClick = onRetry) {
                Text("Retry")
            }
        }
    }
}

/**
 * Netflix-style shimmer effect for loading state
 * Matches the exact layout of the anime list screen
 */
@Composable
fun EmptyContent(modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        // Header Shimmer
        item {
            ShimmerHeader(modifier = Modifier.fillMaxWidth())
        }
        
        // Tabs Shimmer
        item {
            ShimmerTabs(modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp))
        }
        
        // Featured Section Shimmer (Large Poster)
        item {
            ShimmerFeaturedSection(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
        
        item {
            Spacer(modifier = Modifier.height(8.dp))
        }
        
        // Section Header Shimmer
        item {
            ShimmerSectionHeader(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
            )
        }
        
        // Horizontal Row Shimmer
        item {
            ShimmerHorizontalRow(modifier = Modifier.fillMaxWidth())
        }
        
        // Top Rated Section (if applicable)
        item {
            Spacer(modifier = Modifier.height(24.dp))
            ShimmerSectionHeader(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
            )
        }
        item {
            ShimmerHorizontalRow(modifier = Modifier.fillMaxWidth())
        }
        
        // Popular Now Section
        item {
            Spacer(modifier = Modifier.height(24.dp))
            ShimmerSectionHeader(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
            )
        }
        item {
            ShimmerHorizontalRow(modifier = Modifier.fillMaxWidth())
        }
    }
}

/**
 * Shimmer effect modifier - creates animated gradient overlay
 * Netflix-style shimmer with smooth animation
 */
@Composable
fun ShimmerEffect(
    modifier: Modifier = Modifier,
    baseColor: Color = Color(0xFF1A1A1A),
    highlightColor: Color = Color(0xFF2D2D2D)
) {
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    val shimmerTranslate by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1500,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_translate"
    )

    Box(
        modifier = modifier
            .background(baseColor)
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color.Transparent,
                        highlightColor.copy(alpha = 0.6f),
                        Color.Transparent
                    ),
                    start = Offset(shimmerTranslate - 400f, shimmerTranslate - 400f),
                    end = Offset(shimmerTranslate, shimmerTranslate)
                )
            )
    )
}

/**
 * Shimmer Header (For You + Search icon)
 */
@Composable
fun ShimmerHeader(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // "For You" text shimmer (larger, matching headlineLarge)
        ShimmerEffect(
            modifier = Modifier
                .width(140.dp)
                .height(38.dp)
                .clip(RoundedCornerShape(4.dp))
        )
        
        // Search icon shimmer
        ShimmerEffect(
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(16.dp))
        )
    }
}

/**
 * Shimmer Tabs (Shows, Movies, Categories)
 */
@Composable
fun ShimmerTabs(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(12.dp)
    ) {
        repeat(3) {
            ShimmerEffect(
                modifier = Modifier
                    .width(80.dp)
                    .height(36.dp)
                    .clip(RoundedCornerShape(18.dp))
            )
        }
    }
}

/**
 * Shimmer Featured Section (Large Poster)
 * Matches FeaturedAnimeSection dimensions (420dp height)
 */
@Composable
fun ShimmerFeaturedSection(modifier: Modifier = Modifier) {
    // Large poster shimmer - matches FeaturedAnimeSection card
    ShimmerEffect(
        modifier = modifier
            .fillMaxWidth()
            .height(420.dp)
            .clip(RoundedCornerShape(16.dp))
    )
}

/**
 * Shimmer Section Header
 */
@Composable
fun ShimmerSectionHeader(modifier: Modifier = Modifier) {
    ShimmerEffect(
        modifier = modifier
            .width(120.dp)
            .height(24.dp)
            .clip(RoundedCornerShape(4.dp))
    )
}

/**
 * Shimmer Horizontal Row (Multiple cards)
 */
@Composable
fun ShimmerHorizontalRow(modifier: Modifier = Modifier) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 20.dp)
    ) {
        items(6) {
            ShimmerAnimeCard()
        }
    }
}

/**
 * Shimmer Anime Card (matches HorizontalAnimeCard dimensions)
 */
@Composable
fun ShimmerAnimeCard(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        // Card image shimmer
        ShimmerEffect(
            modifier = Modifier
                .width(140.dp)
                .height(210.dp)
                .clip(RoundedCornerShape(8.dp))
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Title shimmer
        ShimmerEffect(
            modifier = Modifier
                .width(120.dp)
                .height(16.dp)
                .clip(RoundedCornerShape(4.dp))
        )
    }
}

