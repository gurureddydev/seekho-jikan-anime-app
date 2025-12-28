package com.woolo.seekhoandroid.presentation.screen.anime_list

import com.woolo.seekhoandroid.domain.model.Anime

/**
 * Get available tabs based on actual anime data
 */
fun getAvailableTabs(animeList: List<Anime>): List<String> {
    val tabs = mutableListOf<String>()
    
    // Check what types exist in the data
    val types = animeList.mapNotNull { it.type }.distinct()
    
    // Always show Shows, Movies, Categories (matching the design)
    tabs.add("Shows")
    tabs.add("Movies")
    
    // Add Categories if we have special types, otherwise show it anyway
    if (types.any { it in listOf("OVA", "ONA", "Special") } || types.isNotEmpty()) {
        tabs.add("Categories")
    }
    
    // Fallback: Use episodes to categorize if type is not available
    if (types.isEmpty()) {
        val hasMovies = animeList.any { it.episodes == 1 }
        val hasShows = animeList.any { it.episodes != null && it.episodes!! > 1 }
        
        // Still show all three tabs as per design
        if (!hasShows && !hasMovies) {
            tabs.add("Categories")
        }
    }
    
    return tabs
}

/**
 * Get available categories/genres from anime list
 */
fun getAvailableCategories(animeList: List<Anime>): List<String> {
    val allGenres = animeList
        .flatMap { it.genres ?: emptyList() }
        .distinct()
        .sorted()
    
    // Add common categories if we have data
    val categories = mutableListOf<String>()
    
    // Add "All" option
    categories.add("All")
    
    // Add popular genres
    val popularGenres = listOf(
        "Action", "Adventure", "Comedy", "Drama", "Fantasy", 
        "Horror", "Mystery", "Romance", "Sci-Fi", "Slice of Life",
        "Sports", "Supernatural", "Thriller"
    )
    
    popularGenres.forEach { genre ->
        if (allGenres.contains(genre)) {
            categories.add(genre)
        }
    }
    
    // Add remaining genres
    allGenres.forEach { genre ->
        if (!categories.contains(genre)) {
            categories.add(genre)
        }
    }
    
    return categories
}

/**
 * Filter anime list based on selected tab and category
 */
fun getFilteredAnimeList(
    animeList: List<Anime>, 
    selectedTab: String,
    selectedCategory: String? = null
): List<Anime> {
    val filteredByTab = when (selectedTab) {
        "Shows" -> animeList.filter { 
            it.type == "TV" || (it.type == null && it.episodes != null && it.episodes!! > 1)
        }
        "Movies" -> animeList.filter { 
            it.type == "Movie" || (it.type == null && it.episodes == 1)
        }
        "Categories" -> animeList // Don't filter by type for categories, filter by genre instead
        else -> animeList
    }
    
    // Filter by selected category if Categories tab is selected
    return if (selectedTab == "Categories" && selectedCategory != null && selectedCategory != "All") {
        filteredByTab.filter { anime ->
            anime.genres?.contains(selectedCategory) == true
        }
    } else {
        filteredByTab
    }
}

/**
 * Get section title based on selected tab and category
 */
fun getSectionTitle(selectedTab: String, selectedCategory: String?): String {
    return when {
        selectedTab == "Categories" && selectedCategory != null && selectedCategory != "All" -> {
            selectedCategory
        }
        selectedTab == "Categories" -> "Categories"
        else -> selectedTab
    }
}

