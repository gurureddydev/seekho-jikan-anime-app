package com.woolo.seekhoandroid.presentation.screen.anime_list

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.woolo.seekhoandroid.presentation.viewmodel.AnimeListViewModel

/**
 * Check if a color is too light for a dark theme background
 */
private fun isTooLight(color: Color): Boolean {
    // Calculate luminance (perceived brightness)
    val luminance = 0.299 * color.red + 0.587 * color.green + 0.114 * color.blue
    return luminance > 0.5f // Too light if luminance > 50%
}

/**
 * Darken a color by a factor (0.0 = no change, 1.0 = black)
 */
private fun darkenColor(color: Color, factor: Float): Color {
    return Color(
        red = (color.red * (1f - factor)).coerceIn(0f, 1f),
        green = (color.green * (1f - factor)).coerceIn(0f, 1f),
        blue = (color.blue * (1f - factor)).coerceIn(0f, 1f),
        alpha = color.alpha
    )
}

/**
 * Desaturate a color by a factor (0.0 = no change, 1.0 = grayscale)
 */
private fun desaturateColor(color: Color, factor: Float): Color {
    val gray = (color.red + color.green + color.blue) / 3f
    return Color(
        red = (color.red * (1f - factor) + gray * factor).coerceIn(0f, 1f),
        green = (color.green * (1f - factor) + gray * factor).coerceIn(0f, 1f),
        blue = (color.blue * (1f - factor) + gray * factor).coerceIn(0f, 1f),
        alpha = color.alpha
    )
}

@Composable
fun AnimeListScreen(
    onAnimeClick: (Int) -> Unit,
    viewModel: AnimeListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val isOnline by viewModel.isOnline.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Initialize selected tab based on available data
    val availableTabs = remember(uiState.animeList) { 
        getAvailableTabs(uiState.animeList) 
    }
    var selectedTab by remember(availableTabs) { 
        mutableStateOf(availableTabs.firstOrNull() ?: "Shows") 
    }
    
    // Categories dropdown state
    var showCategoriesDropdown by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    
    // Search state
    var showSearchScreen by remember { mutableStateOf(false) }
    
    // Get available categories/genres from data
    val availableCategories = remember(uiState.animeList) {
        getAvailableCategories(uiState.animeList)
    }

    LaunchedEffect(isOnline) {
        if (!isOnline) {
            snackbarHostState.showSnackbar(
                message = "📴 Offline mode — showing saved results",
                duration = SnackbarDuration.Short
            )
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    modifier = Modifier.padding(16.dp),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }
    ) { paddingValues ->
        // Calculate featured anime and background color at top level
        val filteredAnimeList = remember(uiState.animeList, selectedTab, selectedCategory) {
            if (uiState.animeList.isNotEmpty()) {
                getFilteredAnimeList(
                    animeList = uiState.animeList, 
                    selectedTab = selectedTab,
                    selectedCategory = if (selectedTab == "Categories") selectedCategory else null
                )
            } else {
                emptyList()
            }
        }
        
        val featuredAnime = remember(filteredAnimeList) {
            filteredAnimeList
                .sortedBy { it.rank ?: Int.MAX_VALUE }
                .firstOrNull() ?: filteredAnimeList.firstOrNull()
        }
        
        // Extract palette colors from featured anime for dynamic background
        val paletteColors = rememberImagePalette(featuredAnime?.imageUrl)
        
        // Netflix-style dynamic background color calculation
        // Netflix uses vibrant colors more prominently, with smart darkening for backgrounds
        val defaultBackgroundColor = Color(0xFF0F0F0F)
        val extractedBackgroundColor = remember(paletteColors) {
            // Netflix prioritizes: darkVibrant > darkMuted > dominant > vibrant
            // Then applies smart darkening to ensure readability
            val darkVibrant = paletteColors.darkVibrant
            val darkMuted = paletteColors.darkMuted
            val dominant = paletteColors.dominant
            val vibrant = paletteColors.vibrant
            
            // Select the best color for background theming
            val primaryColor = when {
                // Prefer darkVibrant (Netflix's favorite for backgrounds)
                darkVibrant != Color.Black && 
                darkVibrant != defaultBackgroundColor &&
                !isTooLight(darkVibrant) -> darkVibrant
                
                // Fallback to darkMuted
                darkMuted != Color.Black && 
                darkMuted != defaultBackgroundColor &&
                !isTooLight(darkMuted) -> darkMuted
                
                // Try dominant if it's dark enough
                dominant != Color.Black && 
                dominant != defaultBackgroundColor &&
                !isTooLight(dominant) -> dominant
                
                // Last resort: vibrant (but we'll darken it significantly)
                vibrant != Color.Black && 
                vibrant != defaultBackgroundColor -> vibrant
                
                else -> defaultBackgroundColor
            }
            
            // Netflix-style: Darken and desaturate the color for background
            // This ensures the background complements the content without overwhelming it
            if (primaryColor != defaultBackgroundColor) {
                val darkenedColor = darkenColor(primaryColor, factor = 0.85f)
                val desaturatedColor = desaturateColor(darkenedColor, factor = 0.6f)
                
                // Blend with default (60% extracted, 40% default) for Netflix-style subtle theming
                Color(
                    red = (desaturatedColor.red * 0.6f + defaultBackgroundColor.red * 0.4f).coerceIn(0f, 1f),
                    green = (desaturatedColor.green * 0.6f + defaultBackgroundColor.green * 0.4f).coerceIn(0f, 1f),
                    blue = (desaturatedColor.blue * 0.6f + defaultBackgroundColor.blue * 0.4f).coerceIn(0f, 1f),
                    alpha = 1f
                )
            } else {
                defaultBackgroundColor
            }
        }
        
        // Animate background color changes smoothly (Netflix-style smooth transitions)
        val animatedBackgroundColor by animateColorAsState(
            targetValue = extractedBackgroundColor,
            animationSpec = tween(durationMillis = 1200), // Slower, more elegant transition
            label = "background_color_animation"
        )
        
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(animatedBackgroundColor)
                .padding(paddingValues)
        ) {
            // Search Screen with animation
            AnimatedVisibility(
                visible = showSearchScreen,
                enter = fadeIn(animationSpec = tween(300)) + slideInVertically(
                    initialOffsetY = { -it },
                    animationSpec = tween(300)
                ),
                exit = fadeOut(animationSpec = tween(300)) + slideOutVertically(
                    targetOffsetY = { -it },
                    animationSpec = tween(300)
                )
            ) {
                SearchScreen(
                    animeList = uiState.animeList,
                    onBackClick = { showSearchScreen = false },
                    onAnimeClick = onAnimeClick,
                    onAISuggestionClick = { suggestion ->
                        // AI suggestion is handled in SearchScreen
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
            
            // Main Content with animation
            AnimatedVisibility(
                visible = !showSearchScreen,
                enter = fadeIn(animationSpec = tween(300)),
                exit = fadeOut(animationSpec = tween(300))
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    when {
                        // Netflix-style: Only show loading if we have no cached data
                        uiState.isLoading && uiState.animeList.isEmpty() -> {
                            CircularProgressIndicator(
                                modifier = Modifier.align(Alignment.Center),
                                color = Color.White
                            )
                        }
                        uiState.error != null && uiState.animeList.isEmpty() -> {
                            val errorMessage = uiState.error
                            ErrorContent(
                                error = errorMessage ?: "Unknown error",
                                onRetry = { viewModel.retry() },
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        uiState.animeList.isEmpty() -> {
                            EmptyContent(modifier = Modifier.fillMaxSize())
                        }
                        else -> {
                            // Memoize sorted lists for sections
                            val topRatedList = remember(filteredAnimeList) {
                                filteredAnimeList
                                    .sortedByDescending { it.score ?: 0.0 }
                                    .take(10)
                            }
                            
                            val popularList = remember(filteredAnimeList) {
                                filteredAnimeList
                                    .sortedBy { it.rank ?: Int.MAX_VALUE }
                                    .take(15)
                            }
                            
                            // Main content with horizontal scrolling sections
                            // Track scroll state for smooth animations
                            val listState = rememberLazyListState()
                            
                            LazyColumn(
                                state = listState,
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(bottom = 16.dp)
                            ) {
                                // Top Section: Personalized Header (For You + Search) - STICKY/FIXED
                                // Netflix-style: Header stays fixed at top with solid background
                                stickyHeader {
                                    TopSection(
                                        userName = "You",
                                        onSearchClick = { showSearchScreen = true },
                                        modifier = Modifier.fillMaxWidth(),
                                        backgroundColor = animatedBackgroundColor
                                    )
                                }

                                // Tab Buttons (scrolls with content, matching Netflix-style)
                                item {
                                    TabSection(
                                        tabs = availableTabs,
                                        selectedTab = selectedTab,
                                        onTabSelected = { tab ->
                                            selectedTab = tab
                                            if (tab == "Categories") {
                                                showCategoriesDropdown = true
                                            } else {
                                                showCategoriesDropdown = false
                                                selectedCategory = null
                                            }
                                        },
                                        showCategoriesDropdown = showCategoriesDropdown,
                                        availableCategories = availableCategories,
                                        selectedCategory = selectedCategory,
                                        onCategorySelected = { category ->
                                            selectedCategory = category
                                            showCategoriesDropdown = false
                                        },
                                        onDismissDropdown = { showCategoriesDropdown = false },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 20.dp, vertical = 8.dp)
                                    )
                                }
                                
                                // Featured Anime Section (Large Poster below tabs)
                                if (featuredAnime != null) {
                                    item {
                                        FeaturedAnimeSection(
                                            anime = featuredAnime,
                                            onAnimeClick = { onAnimeClick(featuredAnime.malId) },
                                            onPlayClick = { onAnimeClick(featuredAnime.malId) },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 16.dp, vertical = 8.dp)
                                        )
                                    }
                                }
                                
                                // Spacing after featured section
                                item {
                                    Spacer(modifier = Modifier.height(8.dp))
                                }

                                // Horizontal Scrolling Sections (filtered by selected tab)
                                item {
                                    SectionHeader(
                                        title = getSectionTitle(selectedTab, selectedCategory),
                                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
                                    )
                                }
                                item {
                                    HorizontalAnimeRow(
                                        animeList = filteredAnimeList.take(15),
                                        onAnimeClick = onAnimeClick,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }

                                // Additional sections based on selected tab
                                if (selectedTab == "Shows" || selectedTab == "Movies") {
                                    item {
                                        SectionHeader("Top Rated", modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp))
                                    }
                                    item {
                                        HorizontalAnimeRow(
                                            animeList = topRatedList,
                                            onAnimeClick = onAnimeClick,
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }
                                }

                                item {
                                    SectionHeader("Popular Now", modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp))
                                }
                                item {
                                    HorizontalAnimeRow(
                                        animeList = popularList,
                                        onAnimeClick = onAnimeClick,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }
                    }
                }
            }
            
            // Full-screen Categories Menu Overlay (only show when search is not visible)
            if (!showSearchScreen && showCategoriesDropdown) {
                CategoriesFullScreenMenu(
                    categories = availableCategories,
                    selectedCategory = selectedCategory,
                    onCategorySelected = { category ->
                        selectedCategory = category
                        showCategoriesDropdown = false
                    },
                    onDismiss = {
                        showCategoriesDropdown = false
                    }
                )
            }
        }
    }
}
