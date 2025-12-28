package com.woolo.seekhoandroid.presentation.screen.anime_list

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import coil.size.Size
import androidx.palette.graphics.Palette
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Data class to hold extracted palette colors
 * Netflix-style theming uses vibrant and dark vibrant colors prominently
 */
data class PaletteColors(
    val dominant: Color = Color.Black,
    val vibrant: Color = Color.Black,
    val darkVibrant: Color = Color.Black,
    val lightVibrant: Color = Color.Black,
    val muted: Color = Color.Black,
    val darkMuted: Color = Color.Black,
    val lightMuted: Color = Color.Black
)

/**
 * Extract dominant colors from image using Palette
 * Uses Coil to load bitmap directly for better color extraction
 * Optimized for performance with proper threading and caching
 */
@Composable
fun rememberImagePalette(imageUrl: String?): PaletteColors {
    val context = LocalContext.current
    // Remember ImageLoader to avoid recreation
    val imageLoader = remember { ImageLoader(context) }
    var paletteColors by remember { mutableStateOf(PaletteColors()) }
    
    LaunchedEffect(imageUrl) {
        if (imageUrl.isNullOrBlank()) {
            paletteColors = PaletteColors()
            return@LaunchedEffect
        }
        
        try {
            // Create image request to get bitmap - use larger size for better color extraction
            // Netflix-style theming benefits from higher quality color extraction
            val imageRequest = ImageRequest.Builder(context)
                .data(imageUrl)
                .size(Size(400, 400)) // Increased size for better color accuracy
                .allowHardware(false) // Critical: Palette needs software bitmap
                .build()
            
            // Execute on IO dispatcher to avoid blocking UI
            val result = withContext(Dispatchers.IO) {
                imageLoader.execute(imageRequest)
            }
            
            if (result is SuccessResult) {
                val drawable = result.drawable
                
                // Extract bitmap from drawable on IO thread
                val bitmap: Bitmap? = when (drawable) {
                    is BitmapDrawable -> {
                        drawable.bitmap
                    }
                    else -> {
                        // Convert drawable to bitmap
                        val width = drawable.intrinsicWidth.takeIf { it > 0 } ?: 200
                        val height = drawable.intrinsicHeight.takeIf { it > 0 } ?: 200
                        try {
                            // Create mutable bitmap with better size for color extraction
                            val bmp = Bitmap.createBitmap(
                                width.coerceAtMost(400),
                                height.coerceAtMost(400),
                                Bitmap.Config.ARGB_8888
                            )
                            val canvas = android.graphics.Canvas(bmp)
                            drawable.setBounds(0, 0, bmp.width, bmp.height)
                            drawable.draw(canvas)
                            bmp
                        } catch (e: Exception) {
                            null
                        }
                    }
                }
                
                if (bitmap != null && !bitmap.isRecycled) {
                    // Generate palette on Default dispatcher (CPU-intensive work)
                    val palette = withContext(Dispatchers.Default) {
                        try {
                            Palette.from(bitmap)
                                .maximumColorCount(16) // More colors for better extraction
                                .generate()
                        } catch (e: Exception) {
                            null
                        }
                    }
                    
                    if (palette != null) {
                        // Extract colors with intelligent fallbacks
                        // Netflix prioritizes vibrant colors for theming
                        val defaultColor = 0xFF1A1A1A.toInt()
                        val dominantColor = palette.getDominantColor(defaultColor)
                        val vibrantColor = palette.getVibrantColor(dominantColor)
                        val darkVibrantColor = palette.getDarkVibrantColor(dominantColor)
                        val lightVibrantColor = palette.getLightVibrantColor(dominantColor)
                        val mutedColor = palette.getMutedColor(dominantColor)
                        val darkMutedColor = palette.getDarkMutedColor(dominantColor)
                        val lightMutedColor = palette.getLightMutedColor(dominantColor)
                        
                        // Update colors on main thread
                        paletteColors = PaletteColors(
                            dominant = Color(dominantColor),
                            vibrant = Color(vibrantColor),
                            darkVibrant = Color(darkVibrantColor),
                            lightVibrant = Color(lightVibrantColor),
                            muted = Color(mutedColor),
                            darkMuted = Color(darkMutedColor),
                            lightMuted = Color(lightMutedColor)
                        )
                    } else {
                        paletteColors = PaletteColors()
                    }
                } else {
                    paletteColors = PaletteColors()
                }
            } else {
                paletteColors = PaletteColors()
            }
        } catch (e: Exception) {
            // Fallback to default colors on error
            paletteColors = PaletteColors()
        }
    }
    
    return paletteColors
}

