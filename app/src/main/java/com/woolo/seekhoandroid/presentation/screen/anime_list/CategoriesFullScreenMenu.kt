package com.woolo.seekhoandroid.presentation.screen.anime_list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush as ComposeBrush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Full-screen Categories Menu Overlay
 * Matches the design from the provided image
 */
@Composable
fun CategoriesFullScreenMenu(
    categories: List<String>,
    selectedCategory: String?,
    onCategorySelected: (String) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                ComposeBrush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0F0F0F), // Dark background
                        Color(0xFF1A1A1A), // Slightly lighter at bottom
                        Color(0xFF0F0F0F).copy(alpha = 0.9f) // Subtle gradient
                    )
                )
            )
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                // Dismiss when clicking outside (optional)
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 60.dp)
        ) {
            // Categories List
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(0.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(categories) { category ->
                    val isSelected = selectedCategory == category
                    
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onCategorySelected(category)
                            }
                            .padding(vertical = 16.dp, horizontal = 4.dp)
                    ) {
                        Text(
                            text = category,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                            color = if (isSelected) {
                                Color.White
                            } else {
                                Color.White.copy(alpha = 0.85f) // Light gray/white
                            },
                            fontSize = 18.sp,
                            letterSpacing = 0.2.sp
                        )
                    }
                    
                    // Divider (subtle)
                    if (category != categories.last()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(0.5.dp)
                                .background(Color.White.copy(alpha = 0.1f))
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }
            }
            
            // Close Button (Large White Circle with X)
            Spacer(modifier = Modifier.height(32.dp))
            
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .size(64.dp)
                    .background(
                        color = Color.White,
                        shape = CircleShape
                    )
                    .clickable {
                        onDismiss()
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = Color.Black,
                    modifier = Modifier.size(32.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

