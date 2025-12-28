package com.woolo.seekhoandroid.presentation.screen.anime_detail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.text.ClickableText
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.woolo.seekhoandroid.domain.model.AnimeDetail
import com.woolo.seekhoandroid.domain.model.Character
import com.woolo.seekhoandroid.presentation.viewmodel.AnimeDetailViewModel
import com.woolo.seekhoandroid.presentation.screen.anime_detail.components.TrailerPlayer
import com.woolo.seekhoandroid.ui.theme.SeekhoAndroidTheme
import timber.log.Timber
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimeDetailScreen(
    malId: Int,
    onBackClick: () -> Unit,
    viewModel: AnimeDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    LaunchedEffect(malId) {
        if (malId > 0) {
            viewModel.loadAnimeDetail(malId)
        }
    }

    val animeDetail = uiState.animeDetail
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        when {
            // Netflix-style: Only show loading if we have no cached data
            uiState.isLoading && uiState.animeDetail == null -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = MaterialTheme.colorScheme.primary
                )
            }
            uiState.error != null -> {
                val errorMessage = uiState.error
                ErrorContent(
                    error = errorMessage ?: "Unknown error",
                    onRetry = { viewModel.retry(malId) },
                    modifier = Modifier.fillMaxSize()
                )
            }
            uiState.animeDetail != null -> {
                val detail = uiState.animeDetail!!
                // Log trailer info for debugging
                LaunchedEffect(detail.trailerYoutubeId) {
                    if (detail.trailerYoutubeId != null) {
                        Timber.d("AnimeDetailScreen: Trailer detected - YouTube ID: ${detail.trailerYoutubeId}")
                        Timber.d("AnimeDetailScreen: Trailer Embed URL: ${detail.trailerEmbedUrl}")
                    } else {
                        Timber.d("AnimeDetailScreen: No trailer available, showing poster")
                    }
                }
                AnimeDetailContent(
                    animeDetail = detail,
                    characters = uiState.characters,
                    isLoadingCharacters = uiState.isLoadingCharacters,
                    charactersError = uiState.charactersError,
                    scrollState = scrollState,
                    onBackClick = onBackClick,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
fun AnimeDetailContent(
    animeDetail: AnimeDetail,
    characters: List<Character>,
    isLoadingCharacters: Boolean,
    charactersError: String?,
    scrollState: androidx.compose.foundation.ScrollState,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var synopsisExpanded by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableStateOf(0) }
    
    // Calculate preview text (approximately 2-3 lines, ~150-200 characters)
    val synopsisText = animeDetail.synopsis ?: ""
    val previewLength = 180 // Characters for preview (approximately 2-3 lines)
    val hasMoreSynopsis = synopsisText.length > previewLength
    val synopsisPreview = if (hasMoreSynopsis) {
        // Find the last space before the limit to avoid cutting words
        val truncated = synopsisText.take(previewLength)
        val lastSpace = truncated.lastIndexOf(' ')
        if (lastSpace > previewLength * 0.7) {
            truncated.take(lastSpace)
        } else {
            truncated
        }
    } else {
        synopsisText
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        // Hero Poster/Trailer Section with Gradient Overlay
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
        ) {
            // Validate and check if trailer is available
            val hasValidTrailer = hasValidTrailer(animeDetail)
            
            if (hasValidTrailer) {
                // Get the YouTube ID (from direct ID or extracted from embed URL)
                val youtubeId = getValidYoutubeId(animeDetail)
                
                if (youtubeId != null) {
                    // Show video player directly in place of poster - video will autoplay
                    // Use the embed URL from API if available (supports youtube-nocookie.com)
                    Box(modifier = Modifier.fillMaxSize()) {
                        TrailerPlayerWithFallback(
                            youtubeId = youtubeId,
                            fallbackImageUrl = animeDetail.largeImageUrl ?: animeDetail.imageUrl,
                            fallbackTitle = animeDetail.title,
                            onBackClick = onBackClick,
                            modifier = Modifier.fillMaxSize(),
                            embedUrl = animeDetail.trailerEmbedUrl
                        )
                        // Back Button overlay
                        IconButton(
                            onClick = onBackClick,
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                } else {
                    // Show poster image if YouTube ID extraction failed
                    HeroPosterWithOverlay(
                        imageUrl = animeDetail.largeImageUrl ?: animeDetail.imageUrl,
                        title = animeDetail.title,
                        onBackClick = onBackClick
                    )
                }
            } else {
                // No valid trailer available, show poster image
                HeroPosterWithOverlay(
                    imageUrl = animeDetail.largeImageUrl ?: animeDetail.imageUrl,
                    title = animeDetail.title,
                    onBackClick = onBackClick
                )
            }
        }

        // Content Section with Dark Background
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Black)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))
            
            // App Branding (like NETFLIX logo)
            Text(
                text = "SEEKHO",
                style = MaterialTheme.typography.labelLarge,
                color = Color(0xFFE50914), // Netflix red color
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Title
            Text(
                text = animeDetail.title,
                style = MaterialTheme.typography.headlineLarge.copy(fontSize = 28.sp),
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.fillMaxWidth()
            )

            if (animeDetail.titleEnglish != null && animeDetail.titleEnglish != animeDetail.title) {
                Text(
                    text = animeDetail.titleEnglish,
                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 16.sp),
                    color = Color(0xFFB3B3B3),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Metadata Row (Year, Rating, Duration, Type, HD)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                animeDetail.year?.let {
                    MetadataBadge(text = it.toString(), color = Color.White)
                }
                animeDetail.rating?.let {
                    val ratingText = it.split(" ").firstOrNull() ?: it
                    MetadataBadge(text = ratingText, color = Color.White)
                }
                animeDetail.episodes?.let {
                    MetadataBadge(text = "$it Episodes", color = Color.White)
                }
                animeDetail.type?.let {
                    MetadataBadge(text = it, color = Color.White)
                }
                MetadataBadge(text = "HD", color = Color.White)
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Action Buttons Row (Play and Download)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Play Button
                Button(
                    onClick = { /* Handle play */ },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Play",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Play",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }

                // Download Button (if applicable)
                if (animeDetail.episodes != null) {
                    Button(
                        onClick = { /* Handle download */ },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2F2F2F),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Download,
                            contentDescription = "Download",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Download",
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Synopsis with Netflix-style "...more" inline expansion
            if (!animeDetail.synopsis.isNullOrEmpty()) {
                ExpandableSynopsis(
                    text = synopsisText,
                    previewText = synopsisPreview,
                    isExpanded = synopsisExpanded,
                    hasMore = hasMoreSynopsis,
                    onExpandClick = { synopsisExpanded = true }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Icons Row (My List, Rate, Share, Download)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ActionIconButton(
                    icon = Icons.Default.Add,
                    label = "My List",
                    onClick = { /* Handle add to list */ }
                )
                ActionIconButton(
                    icon = Icons.Default.ThumbUp,
                    label = "Rate",
                    onClick = { /* Handle rate */ }
                )
                ActionIconButton(
                    icon = Icons.Default.Share,
                    label = "Share",
                    onClick = { /* Handle share */ }
                )
                if (animeDetail.episodes != null) {
                    ActionIconButton(
                        icon = Icons.Filled.Download,
                        label = "Download",
                        onClick = { /* Handle download */ }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Tabs Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                TabButton(
                    text = "Details",
                    isSelected = selectedTab == 0,
                    onClick = { selectedTab = 0 }
                )
                TabButton(
                    text = "More Like This",
                    isSelected = selectedTab == 1,
                    onClick = { selectedTab = 1 }
                )
                if (hasValidTrailer(animeDetail)) {
                    TabButton(
                        text = "Trailer",
                        isSelected = selectedTab == 2,
                        onClick = { selectedTab = 2 }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Tab Content
            when (selectedTab) {
                0 -> {
                    // Details Tab
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 32.dp),
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        // Studios Section
                        if (animeDetail.studios.isNotEmpty()) {
                            Text(
                                text = "Studios: ${animeDetail.studios.joinToString(", ")}",
                                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                                color = Color(0xFFB3B3B3),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        // Genres Section
                        if (animeDetail.genres.isNotEmpty()) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(
                                    text = "Genres",
                                    style = MaterialTheme.typography.titleSmall.copy(fontSize = 16.sp),
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                ChipRow(
                                    items = animeDetail.genres,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }

                        // Main Cast Section
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "Main Cast",
                                style = MaterialTheme.typography.titleSmall.copy(fontSize = 16.sp),
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            when {
                                isLoadingCharacters -> {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .heightIn(min = 200.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(32.dp),
                                            color = MaterialTheme.colorScheme.primary,
                                            strokeWidth = 3.dp
                                        )
                                    }
                                }
                                charactersError != null -> {
                                    Text(
                                        text = "Unable to load cast information",
                                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                                        color = Color(0xFFB3B3B3),
                                        modifier = Modifier.padding(vertical = 8.dp)
                                    )
                                }
                                characters.isEmpty() -> {
                                    Text(
                                        text = "No cast information available",
                                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                                        color = Color(0xFFB3B3B3),
                                        modifier = Modifier.padding(vertical = 8.dp)
                                    )
                                }
                                else -> {
                                    MainCastRow(characters = characters)
                                }
                            }
                        }

                        // Additional Info Section
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            animeDetail.status?.let {
                                InfoRow("Status", it)
                            }
                            animeDetail.source?.let {
                                InfoRow("Source", it)
                            }
                            animeDetail.episodes?.let {
                                InfoRow("Episodes", it.toString())
                            }
                            animeDetail.duration?.let {
                                InfoRow("Duration", it)
                            }
                            if (animeDetail.season != null && animeDetail.year != null) {
                                InfoRow("Aired", "${animeDetail.season} ${animeDetail.year}")
                            } else if (animeDetail.year != null) {
                                InfoRow("Year", animeDetail.year.toString())
                            }
                            animeDetail.score?.let {
                                InfoRow("Score", String.format("%.2f", it))
                            }
                        }
                    }
                }
                1 -> {
                    // More Like This Tab
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 32.dp)
                    ) {
                        Text(
                            text = "More Like This",
                            style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp),
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        Text(
                            text = "Similar anime recommendations will appear here",
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                            color = Color(0xFFB3B3B3)
                        )
                    }
                }
                2 -> {
                    // Trailer Tab
                    val youtubeId = getValidYoutubeId(animeDetail)
                    if (youtubeId != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(16f / 9f)
                                .clip(RoundedCornerShape(8.dp))
                                .padding(bottom = 32.dp)
                        ) {
                            TrailerPlayer(
                                youtubeId = youtubeId,
                                modifier = Modifier.fillMaxSize(),
                                embedUrl = animeDetail.trailerEmbedUrl
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun StatChip(
    icon: ImageVector?,
    text: String,
    color: androidx.compose.ui.graphics.Color
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = color.copy(alpha = 0.15f),
        modifier = Modifier.padding(0.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = color
                )
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Medium,
                color = color
            )
        }
    }
}

@Composable
fun ChipRow(
    items: List<String>,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        items.forEach { item ->
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF2F2F2F),
                modifier = Modifier
            ) {
                Text(
                    text = item,
                    style = MaterialTheme.typography.labelMedium.copy(fontSize = 12.sp),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun InfoCard(
    label: String,
    value: String,
    icon: ImageVector
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

/**
 * Trailer player component with fallback to poster image on error
 * When video is available, it plays directly in place of the poster
 */
@Composable
fun TrailerPlayerWithFallback(
    youtubeId: String,
    fallbackImageUrl: String?,
    fallbackTitle: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    embedUrl: String? = null
) {
    var showPosterInstead by remember { mutableStateOf(false) }
    var errorOccurred by remember { mutableStateOf(false) }
    
    // Show video player directly when available - only fallback to poster on error
    if (showPosterInstead || errorOccurred) {
        HeroPosterWithOverlay(
            imageUrl = fallbackImageUrl,
            title = fallbackTitle,
            onBackClick = onBackClick
        )
    } else {
        // Show video player directly in place of poster - video will autoplay
        Box(modifier = modifier) {
            TrailerPlayer(
                youtubeId = youtubeId,
                modifier = Modifier.fillMaxSize(),
                embedUrl = embedUrl,
                onError = {
                    Timber.w("TrailerPlayerWithFallback: Error loading trailer, falling back to poster")
                    errorOccurred = true
                    showPosterInstead = true
                }
            )
            
            // Gradient Overlay (bottom fade)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.3f),
                                Color.Black.copy(alpha = 0.7f),
                                Color.Black
                            ),
                            startY = 0f,
                            endY = Float.POSITIVE_INFINITY
                        )
                    )
            )
        }
    }
}

/**
 * Hero poster with gradient overlay and back button (Netflix-style)
 */
@Composable
fun HeroPosterWithOverlay(
    imageUrl: String?,
    title: String,
    onBackClick: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        // Poster Image
        if (imageUrl.isNullOrBlank()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF1A1A1A)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No Image Available",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White
                )
            }
        } else {
            AsyncImage(
                model = imageUrl,
                contentDescription = title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        // Gradient Overlay (bottom to top fade)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.3f),
                            Color.Black.copy(alpha = 0.7f),
                            Color.Black
                        ),
                        startY = 0f,
                        endY = Float.POSITIVE_INFINITY
                    )
                )
        )

        // Back Button (top left)
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
fun PosterImage(
    imageUrl: String?,
    title: String
) {
    if (imageUrl.isNullOrBlank()) {
        // Show placeholder if no image URL
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF1A1A1A)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No Image Available",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White
            )
        }
    } else {
        AsyncImage(
            model = imageUrl,
            contentDescription = title,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}

/**
 * Metadata badge (Year, Rating, etc.)
 */
@Composable
fun MetadataBadge(
    text: String,
    color: Color
) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium.copy(fontSize = 12.sp),
        color = color,
        modifier = Modifier
            .background(
                color = Color(0xFF2F2F2F),
                shape = RoundedCornerShape(4.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp)
    )
}

/**
 * Action icon button (My List, Rate, Share, Download)
 */
@Composable
fun ActionIconButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = Color.White,
            modifier = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
            color = Color(0xFFB3B3B3)
        )
    }
}

/**
 * Tab button (Details, More Like This, Trailer)
 */
@Composable
fun TabButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.clickable(onClick = onClick)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge.copy(fontSize = 14.sp),
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) Color.White else Color(0xFFB3B3B3),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        if (isSelected) {
            Box(
                modifier = Modifier
                    .width(IntrinsicSize.Min)
                    .height(2.dp)
                    .background(Color(0xFFE50914)) // Netflix red
            )
        } else {
            Spacer(modifier = Modifier.height(2.dp))
        }
    }
}

/**
 * Expandable synopsis with Netflix-style "...more" inline text
 */
@Composable
fun ExpandableSynopsis(
    text: String,
    previewText: String,
    isExpanded: Boolean,
    hasMore: Boolean,
    onExpandClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(bottom = 16.dp)) {
        if (isExpanded || !hasMore) {
            // Show full text when expanded
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                ),
                color = Color.White,
                textAlign = TextAlign.Left
            )
        } else {
            // Show preview with "...more" inline (Netflix style)
            val annotatedText = buildAnnotatedString {
                // Regular text
                withStyle(
                    style = SpanStyle(
                        color = Color.White
                    )
                ) {
                    append(previewText)
                }
                append(" ")
                // Clickable "...more" suffix
                pushStringAnnotation(
                    tag = "more",
                    annotation = "more"
                )
                withStyle(
                    style = SpanStyle(
                        color = Color(0xFFB3B3B3),
                        fontWeight = FontWeight.Normal
                    )
                ) {
                    append("...more")
                }
                pop()
            }
            
            ClickableText(
                text = annotatedText,
                onClick = { offset ->
                    // Check if click is on the "...more" part
                    annotatedText.getStringAnnotations(
                        tag = "more",
                        start = offset,
                        end = offset
                    ).firstOrNull()?.let {
                        onExpandClick()
                    } ?: run {
                        // If click is after preview text, also expand
                        if (offset >= previewText.length) {
                            onExpandClick()
                        }
                    }
                },
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            )
        }
    }
}

/**
 * Main Cast horizontal scrollable row (Netflix-style)
 */
@Composable
fun MainCastRow(
    characters: List<Character>,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 0.dp)
    ) {
        items(characters) { character ->
            CastItem(character = character)
        }
    }
}

/**
 * Individual cast item (character card)
 */
@Composable
fun CastItem(
    character: Character,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.width(120.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Character Image
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF2F2F2F))
        ) {
            if (character.imageUrl.isNullOrBlank()) {
                // Placeholder
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = Color(0xFFB3B3B3),
                        modifier = Modifier.size(48.dp)
                    )
                }
            } else {
                AsyncImage(
                    model = character.imageUrl,
                    contentDescription = character.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Character Name
        Text(
            text = character.name,
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
            color = Color.White,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth()
        )
        
        // Voice Actor (if available)
        character.voiceActors.firstOrNull()?.let { voiceActor ->
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = voiceActor.name,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                color = Color(0xFFB3B3B3),
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

/**
 * Info row for details section
 */
@Composable
fun InfoRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
            color = Color(0xFFB3B3B3)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
            color = Color.White,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.End
        )
    }
}

/**
 * Validates if anime has a valid trailer available
 */
fun hasValidTrailer(animeDetail: AnimeDetail): Boolean {
    // Check if YouTube ID is valid (not null, not empty, not blank)
    val hasValidYoutubeId = !animeDetail.trailerYoutubeId.isNullOrBlank()
    
    // Check if embed URL exists and can be used to extract ID
    val hasValidEmbedUrl = !animeDetail.trailerEmbedUrl.isNullOrBlank() && 
                          extractYoutubeIdFromUrl(animeDetail.trailerEmbedUrl) != null
    
    val hasTrailer = hasValidYoutubeId || hasValidEmbedUrl
    
    Timber.d("hasValidTrailer: youtubeId=$hasValidYoutubeId, embedUrl=$hasValidEmbedUrl, result=$hasTrailer")
    
    return hasTrailer
}

/**
 * Gets a valid YouTube ID from anime detail (from direct ID or extracted from embed URL)
 */
fun getValidYoutubeId(animeDetail: AnimeDetail): String? {
    // First try direct YouTube ID
    if (!animeDetail.trailerYoutubeId.isNullOrBlank()) {
        Timber.d("getValidYoutubeId: Using direct YouTube ID: ${animeDetail.trailerYoutubeId}")
        return animeDetail.trailerYoutubeId.trim()
    }
    
    // Fallback: Extract from embed URL
    if (!animeDetail.trailerEmbedUrl.isNullOrBlank()) {
        val extractedId = extractYoutubeIdFromUrl(animeDetail.trailerEmbedUrl)
        if (extractedId != null) {
            Timber.d("getValidYoutubeId: Extracted YouTube ID from embed URL: $extractedId")
            return extractedId
        }
    }
    
    Timber.d("getValidYoutubeId: No valid YouTube ID found")
    return null
}

/**
 * Extracts YouTube video ID from various YouTube URL formats
 * Supports both youtube.com and youtube-nocookie.com domains
 */
fun extractYoutubeIdFromUrl(url: String): String? {
    if (url.isBlank()) return null
    
    return try {
        val patterns = listOf(
            "(?:youtube(?:-nocookie)?\\.com\\/embed\\/)([^?&]+)",
            "(?:youtube\\.com\\/watch\\?v=)([^&]+)",
            "(?:youtu\\.be\\/)([^?&]+)",
            "(?:youtube\\.com\\/v\\/)([^?&]+)"
        )
        
        for (pattern in patterns) {
            val regex = Regex(pattern, RegexOption.IGNORE_CASE)
            val match = regex.find(url)
            if (match != null && match.groupValues.size > 1) {
                val extractedId = match.groupValues[1].trim()
                // Validate YouTube ID format (typically 11 characters, alphanumeric and dashes/underscores)
                if (extractedId.isNotBlank() && extractedId.length >= 10) {
                    return extractedId
                }
            }
        }
        null
    } catch (e: Exception) {
        Timber.e(e, "Error extracting YouTube ID from URL: $url")
        null
    }
}

@Composable
fun ErrorContent(
    error: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Error",
                style = MaterialTheme.typography.headlineSmall.copy(fontSize = 24.sp),
                color = Color(0xFFE50914),
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = error,
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 8.dp),
                color = Color.White
            )
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE50914),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(4.dp)
            ) {
                Text("Retry", fontWeight = FontWeight.Bold)
            }
        }
    }
}

// Preview Functions
@Preview(showBackground = true, name = "Anime Detail - Full Content", showSystemUi = true)
@Composable
fun AnimeDetailScreenPreview() {
    SeekhoAndroidTheme {
        Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
            AnimeDetailContent(
                animeDetail = getSampleAnimeDetail(),
                characters = getSampleCharacters(),
                isLoadingCharacters = false,
                charactersError = null,
                scrollState = androidx.compose.foundation.rememberScrollState(),
                onBackClick = {},
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Preview(showBackground = true, name = "Anime Detail - With Trailer")
@Composable
fun AnimeDetailWithTrailerPreview() {
    SeekhoAndroidTheme {
        Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
            AnimeDetailContent(
                animeDetail = getSampleAnimeDetailWithTrailer(),
                characters = getSampleCharacters(),
                isLoadingCharacters = false,
                charactersError = null,
                scrollState = androidx.compose.foundation.rememberScrollState(),
                onBackClick = {},
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Preview(showBackground = true, name = "Anime Detail - Loading")
@Composable
fun AnimeDetailLoadingPreview() {
    SeekhoAndroidTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Color(0xFFE50914))
        }
    }
}

@Preview(showBackground = true, name = "Anime Detail - Error")
@Composable
fun AnimeDetailErrorPreview() {
    SeekhoAndroidTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            ErrorContent(
                error = "Failed to load anime details. Please try again.",
                onRetry = {},
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

// Sample Data
private fun getSampleAnimeDetail(): AnimeDetail {
    return AnimeDetail(
        malId = 1,
        title = "Fullmetal Alchemist: Brotherhood",
        titleEnglish = "Fullmetal Alchemist: Brotherhood",
        titleJapanese = "鋼の錬金術師 FULLMETAL ALCHEMIST",
        imageUrl = "https://cdn.myanimelist.net/images/anime/1223/96541.jpg",
        largeImageUrl = "https://cdn.myanimelist.net/images/anime/1223/96541l.jpg",
        trailerYoutubeId = null,
        trailerEmbedUrl = null,
        synopsis = "After a horrific alchemy experiment goes wrong in the Elric household, brothers Edward and Alphonse are left in a catastrophic new reality. Ignoring the alchemical principle banning human transmutation, the boys attempted to bring their recently deceased mother back to life. Instead, they suffered brutal personal loss: Alphonse's body disintegrated while Edward lost a leg and then sacrificed an arm to keep Alphonse's soul in the physical realm by binding it to a hulking suit of armor.\n\n" +
                "The brothers are rescued by their neighbor Pinako Rockbell and her granddaughter Winry. Known as a bio-mechanical engineering prodigy, Winry creates prosthetic limbs for Edward by utilizing \"automail,\" a tough, versatile metal used in robots and combat armor. After years of training, the Elric brothers set off on a quest to restore their bodies by locating the Philosopher's Stone—a powerful gem that allows an alchemist to defy the traditional laws of Equivalent Exchange.\n\n" +
                "As Edward becomes an infamous alchemist and gains the nickname \"Fullmetal,\" the boys' journey embroils them in a growing conspiracy that could threaten the entire country.",
        genres = listOf("Action", "Adventure", "Drama", "Fantasy", "Magic", "Military", "Shounen"),
        studios = listOf("Bones"),
        episodes = 64,
        score = 9.15,
        scoredBy = 1900000,
        status = "Finished Airing",
        type = "TV",
        source = "Manga",
        duration = "24 min per ep",
        rating = "R - 17+ (violence & profanity)",
        airing = false,
        season = "Spring",
        year = 2009,
        background = "Winner of the Animation Kobe Theme Award (2009) and several other awards, Fullmetal Alchemist: Brotherhood is often heralded as one of the greatest anime series of all time."
    )
}

private fun getSampleAnimeDetailWithTrailer(): AnimeDetail {
    return getSampleAnimeDetail().copy(
        trailerYoutubeId = "JPg-UPxhdoE",
        trailerEmbedUrl = "https://www.youtube.com/embed/JPg-UPxhdoE"
    )
}

// Sample Characters Data
private fun getSampleCharacters(): List<Character> {
    return listOf(
        Character(
            malId = 1,
            name = "Edward Elric",
            imageUrl = "https://cdn.myanimelist.net/images/characters/2/284121.jpg",
            role = "Main",
            voiceActors = listOf(
                com.woolo.seekhoandroid.domain.model.VoiceActor(
                    malId = 101,
                    name = "Romie Park",
                    imageUrl = "https://cdn.myanimelist.net/images/voiceactors/2/50442.jpg",
                    language = "Japanese"
                )
            )
        ),
        Character(
            malId = 2,
            name = "Alphonse Elric",
            imageUrl = "https://cdn.myanimelist.net/images/characters/3/284122.jpg",
            role = "Main",
            voiceActors = listOf(
                com.woolo.seekhoandroid.domain.model.VoiceActor(
                    malId = 102,
                    name = "Rie Kugimiya",
                    imageUrl = "https://cdn.myanimelist.net/images/voiceactors/1/50441.jpg",
                    language = "Japanese"
                )
            )
        ),
        Character(
            malId = 3,
            name = "Roy Mustang",
            imageUrl = "https://cdn.myanimelist.net/images/characters/4/284123.jpg",
            role = "Main",
            voiceActors = listOf(
                com.woolo.seekhoandroid.domain.model.VoiceActor(
                    malId = 103,
                    name = "Shin-ichiro Miki",
                    imageUrl = "https://cdn.myanimelist.net/images/voiceactors/3/50443.jpg",
                    language = "Japanese"
                )
            )
        ),
        Character(
            malId = 4,
            name = "Winry Rockbell",
            imageUrl = "https://cdn.myanimelist.net/images/characters/5/284124.jpg",
            role = "Main",
            voiceActors = listOf(
                com.woolo.seekhoandroid.domain.model.VoiceActor(
                    malId = 104,
                    name = "Megumi Takamoto",
                    imageUrl = "https://cdn.myanimelist.net/images/voiceactors/4/50444.jpg",
                    language = "Japanese"
                )
            )
        )
    )
}
