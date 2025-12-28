package com.woolo.seekhoandroid.presentation.screen.anime_list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.woolo.seekhoandroid.domain.model.Anime

/**
 * Search Screen - Matches the design from screenshot
 * Features: Search bar, AI-powered search suggestions, Learn More button, Search results
 */
@Composable
fun SearchScreen(
    animeList: List<Anime>,
    onBackClick: () -> Unit,
    onAnimeClick: (Int) -> Unit,
    onAISuggestionClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    
    // Filter anime based on search query
    val searchResults = remember(searchQuery, animeList) {
        if (searchQuery.isBlank()) {
            emptyList()
        } else {
            animeList.filter { anime ->
                anime.title.contains(searchQuery, ignoreCase = true) ||
                anime.genres?.any { it.contains(searchQuery, ignoreCase = true) } == true
            }
        }
    }
    
    // Group search results into categories for display
    val topResults = remember(searchResults) {
        searchResults.take(10)
    }
    
    // Group by genres for themed sections
    val romanceResults = remember(searchResults) {
        searchResults.filter { 
            it.genres?.any { genre -> 
                genre.contains("romance", ignoreCase = true) || 
                genre.contains("love", ignoreCase = true)
            } == true
        }.take(10)
    }
    
    val actionResults = remember(searchResults) {
        searchResults.filter { 
            it.genres?.any { genre -> 
                genre.contains("action", ignoreCase = true) || 
                genre.contains("adventure", ignoreCase = true)
            } == true
        }.take(10)
    }
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0F0F0F))
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            // Top Section: Back Arrow + Search Bar + Sparkle Icon (OUTSIDE)
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Back Arrow
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier
                            .size(24.dp)
                            .clickable(onClick = onBackClick)
                    )
                    
                    // Search Bar (without sparkle inside)
                    TextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = {
                            Text(
                                text = "Search shows.",
                                color = Color.White.copy(alpha = 0.6f),
                                fontSize = 16.sp
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = Color.White.copy(alpha = 0.7f),
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Clear",
                                    tint = Color.White.copy(alpha = 0.7f),
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clickable { searchQuery = "" }
                                )
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFF1A1A1A),
                            unfocusedContainerColor = Color(0xFF1A1A1A),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedPlaceholderColor = Color.White.copy(alpha = 0.6f),
                            unfocusedPlaceholderColor = Color.White.copy(alpha = 0.6f)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true,
                        textStyle = TextStyle(
                            fontSize = 16.sp,
                            color = Color.White
                        ),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(
                            onSearch = {
                                // Search is handled automatically via searchQuery state
                            }
                        )
                    )
                    
                    // Sparkle Icon OUTSIDE the search field (to the right)
                    Text(
                        text = "✨",
                        fontSize = 20.sp,
                        color = Color(0xFFFF69B4), // Pink color for sparkle
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }
            
            // Show "Try a new way to search" section only when query is empty
            if (searchQuery.isBlank()) {
                // Spacing
                item {
                    Spacer(modifier = Modifier.height(32.dp))
                }
                
                // "Try a new way to search" Section
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                    ) {
                        // Section Header with BETA badge
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Try a new way to search",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White,
                                fontSize = 20.sp,
                                letterSpacing = 0.2.sp
                            )
                            
                            // BETA Badge
                            Surface(
                                color = Color(0xFF3A3A3A),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "BETA",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    letterSpacing = 0.5.sp
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // AI Search Suggestions - Pill-shaped buttons
                        val aiSuggestions = listOf(
                            "secrets and intrigue",
                            "intro to anime",
                            "flings and love triangles",
                            "holiday favor"
                        )
                        
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            contentPadding = PaddingValues(horizontal = 0.dp)
                        ) {
                            items(aiSuggestions) { suggestion ->
                                Surface(
                                    onClick = { 
                                        searchQuery = suggestion
                                        onAISuggestionClick(suggestion)
                                    },
                                    shape = RoundedCornerShape(20.dp),
                                    color = Color(0xFF1A1A1A),
                                    border = BorderStroke(
                                        width = 1.dp,
                                        color = Color.White.copy(alpha = 0.2f)
                                    ),
                                    modifier = Modifier.height(40.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .padding(horizontal = 16.dp, vertical = 10.dp),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // Sparkle Icon
                                        Text(
                                            text = "✨",
                                            fontSize = 14.sp,
                                            color = Color(0xFFFF69B4)
                                        )
                                        
                                        Text(
                                            text = suggestion,
                                            color = Color.White,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Medium,
                                            letterSpacing = 0.2.sp
                                        )
                                    }
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        // Learn More Button
                        Surface(
                            onClick = { /* Handle Learn More */ },
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF1A1A1A),
                            border = BorderStroke(
                                width = 1.dp,
                                color = Color.White.copy(alpha = 0.3f)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Learn More",
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    letterSpacing = 0.3.sp
                                )
                            }
                        }
                    }
                }
                
                // Spacing
                item {
                    Spacer(modifier = Modifier.height(32.dp))
                }
                
                // Recommended Shows & Movies Section (when no search)
                item {
                    SectionHeader(
                        title = "Recommended Shows & Movies",
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
                    )
                }
                
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Start typing to search...",
                            color = Color.White.copy(alpha = 0.6f),
                            fontSize = 14.sp
                        )
                    }
                }
            } else {
                // Show search results or "no results" message
                if (searchResults.isEmpty()) {
                    // No Results Found - "Oops. We don't have that."
                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Oops. We don't have that.",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 24.sp,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Try searching for another movies, shows, actor, director, or genre.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 14.sp,
                                textAlign = TextAlign.Center,
                                lineHeight = 20.sp
                            )
                        }
                    }
                } else {
                    // Show search results in sections
                    // Top Results Section
                    if (topResults.isNotEmpty()) {
                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                        item {
                            SectionHeader(
                                title = "Top Results",
                                modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
                            )
                        }
                        item {
                            HorizontalAnimeRow(
                                animeList = topResults,
                                onAnimeClick = onAnimeClick,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                    
                    // Romance/Love themed section
                    if (romanceResults.isNotEmpty() && (
                        searchQuery.contains("love", ignoreCase = true) || 
                        searchQuery.contains("romance", ignoreCase = true) ||
                        searchQuery.contains("fling", ignoreCase = true)
                    )) {
                        item {
                            Spacer(modifier = Modifier.height(24.dp))
                        }
                        item {
                            SectionHeader(
                                title = "Love & Obsession",
                                modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
                            )
                        }
                        item {
                            HorizontalAnimeRow(
                                animeList = romanceResults,
                                onAnimeClick = onAnimeClick,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                    
                    // Action/Adventure themed section
                    if (actionResults.isNotEmpty() && (
                        searchQuery.contains("action", ignoreCase = true) ||
                        searchQuery.contains("adventure", ignoreCase = true)
                    )) {
                        item {
                            Spacer(modifier = Modifier.height(24.dp))
                        }
                        item {
                            SectionHeader(
                                title = "Action & Adventure",
                                modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
                            )
                        }
                        item {
                            HorizontalAnimeRow(
                                animeList = actionResults,
                                onAnimeClick = onAnimeClick,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }
}
