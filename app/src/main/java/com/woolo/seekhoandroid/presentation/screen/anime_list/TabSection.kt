package com.woolo.seekhoandroid.presentation.screen.anime_list

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TabSection(
    tabs: List<String>,
    selectedTab: String,
    onTabSelected: (String) -> Unit,
    showCategoriesDropdown: Boolean = false,
    availableCategories: List<String> = emptyList(),
    selectedCategory: String? = null,
    onCategorySelected: (String) -> Unit = {},
    onDismissDropdown: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    // Premium styled navigation buttons matching exact design
    Row(
        modifier = modifier
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        tabs.forEach { tab ->
            val isSelected = selectedTab == tab
            val isCategories = tab == "Categories"
            
            Surface(
                onClick = { 
                    onTabSelected(tab)
                },
                shape = RoundedCornerShape(20.dp), // More rounded, oval shape
                color = Color.Transparent, // Transparent background
                border = BorderStroke(
                    width = 1.dp,
                    color = Color.White.copy(alpha = 0.3f) // White border
                ),
                modifier = Modifier
                    .height(36.dp)
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 18.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Text(
                        text = if (isCategories && selectedCategory != null && selectedCategory != "All") {
                            selectedCategory
                        } else {
                            tab
                        },
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Medium,
                        color = Color.White,
                        fontSize = 14.sp,
                        letterSpacing = 0.2.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    // Dropdown chevron for Categories
                    if (isCategories) {
                        Icon(
                            imageVector = if (showCategoriesDropdown) {
                                Icons.Default.KeyboardArrowUp
                            } else {
                                Icons.Default.KeyboardArrowDown
                            },
                            contentDescription = "Dropdown",
                            tint = Color.White.copy(alpha = 0.8f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

