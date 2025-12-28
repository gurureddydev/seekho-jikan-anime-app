package com.woolo.seekhoandroid.presentation.screen.anime_list

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.size.Size
import com.woolo.seekhoandroid.domain.model.Anime

@Composable
fun HorizontalAnimeRow(
    animeList: List<Anime>,
    onAnimeClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 20.dp)
    ) {
        items(
            items = animeList,
            key = { anime -> anime.malId } // Add key for better recomposition performance
        ) { anime ->
            HorizontalAnimeCard(
                anime = anime,
                onClick = { onAnimeClick(anime.malId) }
            )
        }
    }
}

@Composable
fun HorizontalAnimeCard(
    anime: Anime,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = tween(150), label = "scale"
    )

    Box(
        modifier = modifier
            .width(140.dp)
            .height(210.dp)
            .scale(scale)
            .clickable(
                onClick = onClick,
                interactionSource = interactionSource,
                indication = null
            )
    ) {
        // Poster Image - Optimized with size constraints
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(anime.imageUrl)
                .size(Size(280, 420)) // Constrain size for card (140dp * 2 = 280px, 210dp * 2 = 420px)
                .build(),
            contentDescription = anime.title,
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )

        // Gradient overlay at bottom for text readability
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.7f)
                        ),
                        startY = 0f,
                        endY = Float.POSITIVE_INFINITY
                    )
                )
        )

        // TOP 10 Badge (if rank <= 10)
        if (anime.rank != null && anime.rank <= 10) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
            ) {
                Surface(
                    color = Color(0xFFFF0000),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = "TOP ${anime.rank}",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                        fontSize = 10.sp
                    )
                }
            }
        }

        // Bottom Banner (New Season / Recently Added)
        val bannerText = when {
            anime.rank != null && anime.rank <= 5 -> "New Season"
            anime.rank != null && anime.rank <= 10 -> "Recently Added"
            else -> null
        }

        if (bannerText != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .background(Color(0xFFFF0000))
                    .padding(vertical = 4.dp, horizontal = 8.dp)
            ) {
                Text(
                    text = bannerText,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 10.sp
                )
            }
        }

        // Title Overlay
        Text(
            text = anime.title,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = Color.White,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(horizontal = 8.dp, vertical = if (bannerText != null) 24.dp else 8.dp)
                .fillMaxWidth()
        )
    }
}

