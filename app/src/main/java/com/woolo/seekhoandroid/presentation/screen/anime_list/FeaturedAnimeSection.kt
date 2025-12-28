package com.woolo.seekhoandroid.presentation.screen.anime_list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.size.Size
import com.woolo.seekhoandroid.domain.model.Anime

/**
 * Featured Anime Section - Large poster (Netflix-style)
 * Card design with dynamic color extraction from image
 */
@Composable
fun FeaturedAnimeSection(
    anime: Anime,
    onAnimeClick: () -> Unit,
    onPlayClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Extract colors from image
    val paletteColors = rememberImagePalette(anime.imageUrl)
    
    // Use extracted colors for gradient
    // Always use the extracted colors - they have fallbacks built in
    val gradientStartColor = paletteColors.darkVibrant.copy(alpha = 0.0f)
    val gradientMidColor = paletteColors.darkVibrant.copy(alpha = 0.25f)
    val gradientMid2Color = paletteColors.darkMuted.copy(alpha = 0.55f)
    val gradientEndColor = paletteColors.darkMuted.copy(alpha = 0.85f)
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(420.dp)
            .clickable(onClick = onAnimeClick),
        shape = RoundedCornerShape(2.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Large Poster Background - Optimized with size constraints
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(anime.imageUrl)
                    .size(Size(800, 800)) // Constrain size for better performance
                    .build(),
                contentDescription = anime.title,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )
            
            // Dynamic gradient overlay using extracted colors from image
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                gradientStartColor,
                                gradientMidColor,
                                gradientMid2Color,
                                gradientEndColor
                            ),
                            startY = 0f,
                            endY = Float.POSITIVE_INFINITY
                        )
                    )
            )
            
            // Bottom Content: Title, Tags, and Action Buttons
            // Exact spacing matching the design
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 32.dp)
            ) {
                // Title - Large, bold, uppercase
                Text(
                    text = anime.title.uppercase(),
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 32.sp,
                    letterSpacing = 1.sp,
                    lineHeight = 36.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Tags (Genres) - Bullet separated, matching design
                val tags = anime.genres?.take(3) ?: emptyList()
                if (tags.isNotEmpty()) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        tags.forEachIndexed { index, tag ->
                            Text(
                                text = tag,
                                color = Color.White.copy(alpha = 0.95f),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Normal,
                                letterSpacing = 0.2.sp
                            )
                            if (index < tags.size - 1) {
                                Text(
                                    text = "•",
                                    color = Color.White.copy(alpha = 0.8f),
                                    fontSize = 15.sp,
                                    modifier = Modifier.padding(horizontal = 2.dp)
                                )
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Action Buttons: Play and My List - Exact spacing and sizing
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Play Button (White with Black Icon) - Matching design exactly
                    Surface(
                        onClick = onPlayClick,
                        shape = RoundedCornerShape(6.dp),
                        color = Color.White,
                        modifier = Modifier
                            .height(42.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(horizontal = 20.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Play",
                                tint = Color.Black,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "Play",
                                color = Color.Black,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                letterSpacing = 0.3.sp
                            )
                        }
                    }
                    
                    // My List Button (Dark Gray with White Icon) - Matching design exactly
                    Surface(
                        onClick = { /* Handle My List */ },
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFF3A3A3A).copy(alpha = 0.9f),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.3f)),
                        modifier = Modifier
                            .height(42.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(horizontal = 20.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "My List",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "My List",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                letterSpacing = 0.3.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

