package com.woolo.seekhoandroid.presentation.screen.anime_list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TopSection(
    userName: String = "You", // Can be personalized later
    onSearchClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color(0xFF0F0F0F)
) {
    // Fixed solid background for Netflix-style header
    // Always fully opaque for consistent visibility
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(backgroundColor) // Solid fixed background
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            androidx.compose.foundation.layout.Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Personalized Greeting - Exact spacing and alignment
                Text(
                    text = "For $userName",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 32.sp,
                    letterSpacing = (-0.5).sp,
                    lineHeight = 38.sp
                )
                
                // Search Icon with Pink Sparkle - Exact positioning, clickable
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clickable(onClick = onSearchClick),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                    // Pink Sparkle Decoration
                    Text(
                        text = "✨",
                        fontSize = 12.sp,
                        color = Color(0xFFFF69B4), // Pink color for sparkle
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(end = 2.dp, top = 2.dp)
                    )
                }
            }
        }
    }
}

