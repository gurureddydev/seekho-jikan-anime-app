package com.woolo.seekhoandroid.presentation.screen.anime_detail.components

import android.app.Activity
import android.content.pm.ActivityInfo
import android.view.View
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import timber.log.Timber
import com.woolo.seekhoandroid.ui.theme.SeekhoAndroidTheme

/**
 * Error types for better error messaging
 */
enum class TrailerErrorType {
    NETWORK_ERROR,
    VIDEO_UNAVAILABLE,
    PLAYER_ERROR,
    EMBEDDING_DISABLED,
    UNKNOWN_ERROR
}

/**
 * Gets user-friendly error message based on error type
 */
fun getErrorMessage(errorType: TrailerErrorType): String {
    return when (errorType) {
        TrailerErrorType.NETWORK_ERROR -> 
            "Network connection error\nPlease check your internet connection and try again"
        TrailerErrorType.VIDEO_UNAVAILABLE -> 
            "This trailer is unavailable\nIt may have been removed or is not available in your region"
        TrailerErrorType.EMBEDDING_DISABLED -> 
            "This trailer cannot be played in the app\nEmbedding may be disabled for this video"
        TrailerErrorType.PLAYER_ERROR -> 
            "Unable to play trailer\nPlease try again later"
        TrailerErrorType.UNKNOWN_ERROR -> 
            "An error occurred while loading the trailer\nPlease try again"
    }
}

/**
 * YouTube Player States (from YouTube IFrame API)
 */
object YouTubePlayerState {
    const val UNSTARTED = -1
    const val ENDED = 0
    const val PLAYING = 1
    const val PAUSED = 2
    const val BUFFERING = 3
    const val CUED = 5
}

/**
 * YouTube Player Error Codes (from YouTube IFrame API)
 */
object YouTubePlayerError {
    const val INVALID_PARAM = 2
    const val HTML5_ERROR = 5
    const val VIDEO_NOT_FOUND = 100
    const val VIDEO_NOT_PLAYABLE_IN_EMBED = 101
    const val VIDEO_NOT_ALLOWED = 150
}

/**
 * Production-ready YouTube IFrame Player using YouTube IFrame API
 * Supports autoplay, Media Integrity API, and works on Android 14+
 */
@Composable
fun TrailerPlayer(
    youtubeId: String,
    modifier: Modifier = Modifier,
    onError: (() -> Unit)? = null,
    enableFullscreen: Boolean = true,
    embedUrl: String? = null
) {
    var isLoading by remember { mutableStateOf(true) }
    var hasError by remember { mutableStateOf(false) }
    var errorType by remember { mutableStateOf<TrailerErrorType?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var retryKey by remember { mutableStateOf(0) }
    var isFullscreen by remember { mutableStateOf(false) }
    
    val context = LocalContext.current
    val activity = context as? Activity
    val lifecycleOwner = LocalLifecycleOwner.current

    // Extract YouTube ID from embed URL if needed
    val finalVideoId = remember(embedUrl, youtubeId) {
        if (!embedUrl.isNullOrBlank()) {
            // Try to extract ID from embed URL
            val extractedId = extractYoutubeIdFromUrl(embedUrl)
            val result = extractedId ?: youtubeId
            Timber.d("TrailerPlayer: Extracted video ID from embed URL: $extractedId, using: $result")
            result
        } else {
            Timber.d("TrailerPlayer: Using provided YouTube ID: $youtubeId")
            youtubeId
        }
    }

    Timber.d("TrailerPlayer: Final video ID to load: $finalVideoId")
    
    // Validate video ID
    if (finalVideoId.isBlank()) {
        Timber.e("TrailerPlayer: Invalid video ID - empty or blank")
    }

    // Create JavaScript interface for YouTube API callbacks
    val jsInterface = remember {
        object {
            @JavascriptInterface
            fun onState(state: Int) {
                Timber.d("TrailerPlayer: YouTube state changed: $state")
                when (state) {
                    YouTubePlayerState.PLAYING -> {
                        isLoading = false
                        hasError = false
                    }
                    YouTubePlayerState.BUFFERING -> {
                        isLoading = true
                    }
                    YouTubePlayerState.ENDED -> {
                        isLoading = false
                    }
                    YouTubePlayerState.PAUSED -> {
                        isLoading = false
                    }
                }
            }

            @JavascriptInterface
            fun onError(errorCode: Int) {
                Timber.e("TrailerPlayer: YouTube error code: $errorCode")
                isLoading = false
                hasError = true
                
                errorType = when (errorCode) {
                    YouTubePlayerError.VIDEO_NOT_FOUND -> TrailerErrorType.VIDEO_UNAVAILABLE
                    YouTubePlayerError.VIDEO_NOT_PLAYABLE_IN_EMBED -> TrailerErrorType.EMBEDDING_DISABLED
                    YouTubePlayerError.VIDEO_NOT_ALLOWED -> TrailerErrorType.EMBEDDING_DISABLED
                    YouTubePlayerError.INVALID_PARAM -> TrailerErrorType.PLAYER_ERROR
                    YouTubePlayerError.HTML5_ERROR -> TrailerErrorType.PLAYER_ERROR
                    else -> TrailerErrorType.UNKNOWN_ERROR
                }
                errorMessage = getErrorMessage(errorType ?: TrailerErrorType.UNKNOWN_ERROR)
                onError?.invoke()
            }

            @JavascriptInterface
            fun onReady() {
                Timber.d("TrailerPlayer: YouTube player ready")
                isLoading = false
                hasError = false
            }
        }
    }

    Box(modifier = modifier) {
        AndroidView(
            factory = { ctx ->
                WebView(ctx).apply {
                    // Add JavaScript interface
                    addJavascriptInterface(jsInterface, "Android")
                    
                    webViewClient = object : WebViewClient() {
                        override fun onPageStarted(view: WebView?, url: String?, favicon: android.graphics.Bitmap?) {
                            super.onPageStarted(view, url, favicon)
                            isLoading = true
                            hasError = false
                            Timber.d("TrailerPlayer: Page started loading: $url")
                        }

                        override fun onPageFinished(view: WebView?, url: String?) {
                            super.onPageFinished(view, url)
                            Timber.d("TrailerPlayer: Page finished loading: $url")
                            
                            // Load video - try multiple times to ensure API is ready
                            var attempts = 0
                            val maxAttempts = 10
                            
                            fun tryLoadVideo() {
                                attempts++
                                Timber.d("TrailerPlayer: Attempting to load video (attempt $attempts/$maxAttempts), video ID: $finalVideoId")
                                view?.evaluateJavascript("""
                                    (function() {
                                        console.log('Checking for play function, video ID: $finalVideoId');
                                        if (typeof play === 'function') {
                                            console.log('Calling play() with video ID: $finalVideoId');
                                            play('$finalVideoId');
                                            return true;
                                        } else {
                                            console.log('play function not available yet');
                                            return false;
                                        }
                                    })();
                                """.trimIndent()) { result ->
                                    val success = result?.trim() == "true"
                                    Timber.d("TrailerPlayer: Load attempt result: $success")
                                    if (!success && attempts < maxAttempts) {
                                        // Retry after delay if API not ready
                                        Timber.d("TrailerPlayer: Retrying in 200ms...")
                                        view?.postDelayed({ tryLoadVideo() }, 200)
                                    } else if (!success) {
                                        Timber.e("TrailerPlayer: Failed to load video after $maxAttempts attempts")
                                        hasError = true
                                        errorType = TrailerErrorType.PLAYER_ERROR
                                        errorMessage = getErrorMessage(TrailerErrorType.PLAYER_ERROR)
                                        onError?.invoke()
                                    } else {
                                        Timber.d("TrailerPlayer: Successfully called play() function")
                                    }
                                }
                            }
                            
                            // Start trying to load video
                            view?.postDelayed({ tryLoadVideo() }, 300)
                        }

                        override fun onReceivedError(
                            view: WebView?,
                            errorCode: Int,
                            description: String?,
                            failingUrl: String?
                        ) {
                            super.onReceivedError(view, errorCode, description, failingUrl)
                            isLoading = false
                            hasError = true
                            errorType = TrailerErrorType.NETWORK_ERROR
                            errorMessage = getErrorMessage(TrailerErrorType.NETWORK_ERROR)
                            Timber.e("TrailerPlayer: WebView error - Code: $errorCode, Description: $description")
                            onError?.invoke()
                        }
                    }

                    webChromeClient = object : WebChromeClient() {
                        private var customView: View? = null
                        private var customViewCallback: CustomViewCallback? = null
                        private var originalOrientation: Int = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED

                        override fun onConsoleMessage(consoleMessage: android.webkit.ConsoleMessage?): Boolean {
                            consoleMessage?.let {
                                Timber.d("TrailerPlayer: Console [${it.messageLevel()}] ${it.message()} -- From line ${it.lineNumber()} of ${it.sourceId()}")
                            }
                            return true
                        }

                        override fun onProgressChanged(view: WebView?, newProgress: Int) {
                            super.onProgressChanged(view, newProgress)
                            if (newProgress == 100) {
                                Timber.d("TrailerPlayer: Page loaded 100%")
                            }
                        }

                        override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {
                            if (customView != null) {
                                onHideCustomView()
                                return
                            }
                            
                            customView = view
                            customViewCallback = callback
                            
                            originalOrientation = activity?.requestedOrientation 
                                ?: ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                            
                            if (enableFullscreen && activity != null) {
                                isFullscreen = true
                                activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                                
                                val decorView = activity.window.decorView as ViewGroup
                                decorView.addView(customView, ViewGroup.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.MATCH_PARENT
                                ))
                                
                                val uiOptions = (View.SYSTEM_UI_FLAG_FULLSCREEN
                                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                                        or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
                                decorView.systemUiVisibility = uiOptions
                                
                                Timber.d("TrailerPlayer: Entered fullscreen mode")
                            }
                        }

                        override fun onHideCustomView() {
                            customView?.let { view ->
                                if (enableFullscreen && activity != null) {
                                    val decorView = activity.window.decorView as ViewGroup
                                    decorView.removeView(view)
                                    
                                    val uiOptions = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
                                    activity.window.decorView.systemUiVisibility = uiOptions
                                    
                                    activity.requestedOrientation = originalOrientation
                                    
                                    isFullscreen = false
                                    Timber.d("TrailerPlayer: Exited fullscreen mode")
                                }
                                
                                customViewCallback?.onCustomViewHidden()
                                customView = null
                                customViewCallback = null
                            }
                        }
                    }

                    settings.apply {
                        javaScriptEnabled = true
                        domStorageEnabled = true
                        databaseEnabled = true
                        mediaPlaybackRequiresUserGesture = false
                        loadsImagesAutomatically = true
                        allowFileAccess = true
                        allowContentAccess = true
                        setSupportZoom(false)
                        builtInZoomControls = false
                        displayZoomControls = false
                        useWideViewPort = true
                        loadWithOverviewMode = true
                        mixedContentMode = android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                        // Enable hardware acceleration hints
                        setRenderPriority(android.webkit.WebSettings.RenderPriority.HIGH)
                    }
                    
                    // Enable hardware acceleration for better video playback
                    setLayerType(View.LAYER_TYPE_HARDWARE, null)

                    Timber.d("TrailerPlayer: Loading HTML player")
                    loadUrl("file:///android_asset/youtube_player.html")
                }
            },
            modifier = Modifier.fillMaxSize(),
            update = { webView ->
                // Update video when ID changes or on retry
                if (retryKey > 0 || finalVideoId.isNotEmpty()) {
                    webView.evaluateJavascript("""
                        (function() {
                            if (typeof play === 'function') {
                                play('$finalVideoId');
                            } else if (typeof player !== 'undefined' && player && player.loadVideoById) {
                                player.loadVideoById('$finalVideoId');
                            }
                        })();
                    """.trimIndent(), null)
                }
            }
        )

        // Loading indicator
        if (isLoading && !hasError) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f))
                    .zIndex(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                    Text(
                        text = "Loading trailer...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }

        // Error state with retry option
        if (hasError) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.9f))
                    .zIndex(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .padding(16.dp)
                        .clickable {
                            // Retry loading
                            retryKey++
                            hasError = false
                            isLoading = true
                            errorType = null
                            errorMessage = null
                        }
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Retry",
                        tint = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = errorMessage ?: getErrorMessage(TrailerErrorType.UNKNOWN_ERROR),
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = "Tap to retry",
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }

    // Handle lifecycle
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> {
                    Timber.d("TrailerPlayer: Lifecycle - ON_PAUSE")
                }
                Lifecycle.Event.ON_RESUME -> {
                    Timber.d("TrailerPlayer: Lifecycle - ON_RESUME")
                }
                Lifecycle.Event.ON_DESTROY -> {
                    Timber.d("TrailerPlayer: Lifecycle - ON_DESTROY")
                    if (isFullscreen) {
                        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                    }
                }
                else -> {}
            }
        }
        
        lifecycleOwner.lifecycle.addObserver(observer)
        
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            if (isFullscreen) {
                activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            }
        }
    }
}

/**
 * Extracts YouTube video ID from various YouTube URL formats
 * Supports both youtube.com and youtube-nocookie.com domains
 */
private fun extractYoutubeIdFromUrl(url: String): String? {
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

@Preview(showBackground = true, name = "Trailer Player Preview")
@Composable
fun TrailerPlayerPreview() {
    SeekhoAndroidTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            TrailerPlayer(
                youtubeId = "JPg-UPxhdoE",
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
