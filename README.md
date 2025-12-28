# Seekho Android - Anime Discovery App

A modern Android application built with Jetpack Compose that fetches and displays anime information from the Jikan API, with full offline support and robust error handling.

## 📱 Demo

Watch the app in action:

![App Demo](demo/app_demo.gif)

**Full video:** [📹 Watch Full HD Video](demo/screen_recording.mp4)

**Note:** The demo shows the app's key features including anime list browsing, detail view with trailer playback, character information, and Netflix-style UI.

## Features Implemented

### Core Features
- ✅ **Anime List Screen**: Displays top-rated anime in a responsive grid layout
  - Shows anime title, poster image, number of episodes, and rating
  - Lazy loading with LazyVerticalGrid for optimal performance
  - Pull-to-refresh functionality
  - Loading states and error handling with retry mechanism

- ✅ **Anime Detail Screen**: Comprehensive anime information display with Netflix-style UI
  - Advanced YouTube trailer player using YouTube IFrame API
  - Fullscreen support with automatic landscape orientation
  - Autoplay trailers (muted) with smooth fallback to poster image
  - Tab-based navigation (Details, More Like This, Trailer)
  - Main cast/characters section with voice actors (horizontal scrollable)
  - Expandable synopsis with inline "...more" text (Netflix-style)
  - Action buttons: Play, Download, My List, Rate, Share
  - Full synopsis, genres, studios, and additional metadata
  - All details: episodes, rating, status, type, source, duration, etc.
  - Dark theme with gradient overlays

- ✅ **Offline Support**: Full offline functionality with Room database
  - Caches anime list and details locally
  - Automatic sync when network becomes available
  - Seamless transition between online/offline modes
  - Network connectivity observer with real-time updates

- ✅ **Character/Cast Information**: Full character and voice actor support
  - Fetches character data from `/v4/anime/{id}/characters` endpoint
  - Displays character images, names, and roles
  - Shows voice actors with their language
  - Horizontal scrollable cast row (Netflix-style)
  - Loading states and error handling for character data

- ✅ **Advanced Trailer Player**: Production-ready YouTube IFrame API implementation
  - YouTube IFrame API integration with JavaScript interface
  - Fullscreen support with automatic landscape orientation
  - Autoplay trailers (muted) for immersive experience
  - Comprehensive error handling with categorized error types:
    - Network errors
    - Video unavailable
    - Embedding disabled
    - Player errors
  - Retry mechanism with user-friendly error messages
  - YouTube ID extraction from various URL formats (youtube.com, youtube-nocookie.com)
  - Lifecycle-aware player management (pause/resume/destroy)
  - Loading states with visual indicators
  - Hardware acceleration for smooth playback

- ✅ **Background Sync**: WorkManager integration
  - Periodic background sync every 4 hours
  - Syncs only when network is available
  - Automatic retry on failure

### Architecture & Design Patterns
- ✅ **MVVM Architecture**: Clean separation of concerns
  - ViewModels with StateFlow for reactive UI updates
  - Repository pattern for data abstraction
  - UseCases for business logic encapsulation

- ✅ **Dependency Injection**: Hilt for DI
  - Singleton components properly scoped
  - ViewModels, Repositories, and UseCases injected
  - Network and Database modules configured

- ✅ **Error Handling**: Comprehensive error management
  - Sealed Result class (Success, Error, Loading)
  - Retry mechanisms for failed operations
  - User-friendly error messages with specific error types
  - Graceful fallback to cached data
  - Trailer player error handling with categorized errors (network, video unavailable, embedding disabled, etc.)
  - Smart loading states: shows cached data while refreshing in background

### Libraries Used
- **Jetpack Compose**: Modern declarative UI
- **Material Design 3**: Latest Material Design components
- **Hilt**: Dependency injection
- **Retrofit + OkHttp**: Network calls with logging interceptor
- **Moshi**: JSON parsing with Kotlin support
- **Room**: Local database with Flow support
- **Coil**: Efficient image loading for Compose
- **Navigation Compose**: Type-safe navigation
- **WorkManager**: Background sync
- **Timber**: Structured logging

## Assumptions Made

1. **UI Framework**: Used Jetpack Compose instead of XML for modern, declarative UI development
2. **UI Design**: Implemented Netflix-style UI design with dark theme, gradient overlays, and immersive viewing experience
3. **Trailer Playback**: Implemented YouTube trailer playback using YouTube IFrame API via WebView. Features include:
   - Fullscreen support with automatic landscape orientation
   - Autoplay (muted) for better UX
   - Comprehensive error handling with specific error types
   - Retry mechanism with user-friendly messages
   - Support for extracting YouTube ID from embed URLs (supports youtube.com and youtube-nocookie.com)
   - Lifecycle-aware player management
4. **Pagination**: Currently fetches the first page of top anime. The API supports pagination, but infinite scrolling was not implemented to keep the scope manageable
5. **Main Cast**: Character and voice actor information is now fully implemented using the `/v4/anime/{id}/characters` endpoint
6. **Image Loading**: Used Coil instead of Glide/Picasso as it's more Compose-friendly and performant
7. **Min SDK**: Set to 24 (Android 7.0) to support a wide range of devices while maintaining modern features
8. **Network Security**: Enabled `usesCleartextTraffic` for development. In production, this should be disabled and use HTTPS only
9. **Rate Limiting**: Jikan API has rate limits (3 requests/second, 2 requests/second after). The app includes delays and error handling, but doesn't implement exponential backoff retry logic

## Known Limitations

1. **Trailer Playback**: YouTube trailers use YouTube IFrame API via WebView. While this implementation is production-ready with fullscreen support and error handling, some devices may have limitations with WebView video playback. For even better performance, consider implementing the native YouTube Android Player API library
2. **Pagination**: Only the first page of top anime is loaded. Infinite scrolling or pagination controls are not implemented
3. **Image Caching**: While Coil handles image caching, there's no explicit offline image caching strategy beyond Coil's default behavior
4. **Sync Frequency**: Background sync runs every 4 hours. This is configurable but may not be optimal for all use cases
5. **Error Recovery**: While retry mechanisms are in place, there's no exponential backoff for API rate limit errors
6. **Testing**: Unit tests and instrumented tests are set up but not fully implemented. The project structure supports testing with JUnit, Mockito, and Turbine
7. **More Like This**: The "More Like This" tab is currently a placeholder. Similar anime recommendations are not yet implemented

## Project Structure

```
app/src/main/java/com/woolo/seekhoandroid/
├── data/
│   ├── local/          # Room database (entities, DAOs, converters)
│   ├── remote/         # API service and DTOs
│   ├── mapper/         # Data mapping functions
│   └── repository/     # Repository implementation
├── domain/
│   ├── model/          # Domain models
│   └── usecase/        # Business logic use cases
├── presentation/
│   ├── navigation/     # Navigation setup
│   ├── screen/         # UI screens (List, Detail)
│   └── viewmodel/      # ViewModels
├── di/                 # Hilt dependency injection modules
├── util/               # Utilities (Result, NetworkObserver, etc.)
├── worker/             # WorkManager workers
├── MainActivity.kt
└── SeekhoApplication.kt
```

### Minimum Requirements
- **Android Studio**: Hedgehog (2023.1.1) or later
- **JDK**: Java 11 or higher
- **Android SDK**: API Level 36 (Android 15) for compilation
- **Internet Connection**: Required for downloading dependencies and API calls

### Quick Setup (3 Steps)

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd SeekhoAndroid
   ```

2. **Open in Android Studio**
   - Launch Android Studio
   - Select **File → Open**
   - Choose the `SeekhoAndroid` folder
   - Wait for Gradle sync to complete (first time: 5-10 minutes)

3. **Run the app**
   - Connect a device or start an emulator
   - Click the green **Run** button
   - The app will build, install, and launch automatically

**That's it!** The project uses Gradle wrapper, so no additional setup is needed.

## Detailed Setup Instructions

### Step 1: Clone the Repository

```bash
git clone <repository-url>
cd SeekhoAndroid
```

### Step 2: Open in Android Studio

1. Launch Android Studio
2. Select **File → Open**
3. Navigate to the cloned `SeekhoAndroid` directory
4. Click **OK**

### Step 3: Configure Android SDK

Android Studio will automatically detect if the Android SDK is missing. If prompted:

1. Click **Install missing SDK components**
2. Ensure the following are installed:
   - Android SDK Platform 36
   - Android SDK Build-Tools
   - Android SDK Platform-Tools

**Note**: The `local.properties` file (containing SDK path) will be automatically generated by Android Studio. This file is already in `.gitignore` and should not be committed.

### Step 4: Sync Gradle

1. Android Studio will automatically prompt to sync Gradle files
2. If not, click **File → Sync Project with Gradle Files**
3. Wait for dependencies to download (first sync may take several minutes)

### Step 5: Build the Project

**Option A: Using Android Studio**
- Click **Build → Make Project** (or press `Ctrl+F9` / `Cmd+F9`)
- Wait for the build to complete

**Option B: Using Command Line**
```bash
# On macOS/Linux
./gradlew build

# On Windows
gradlew.bat build
```

### Step 6: Run the App

1. Connect an Android device or start an emulator
2. Click **Run → Run 'app'** (or press `Shift+F10` / `Shift+F10`)
3. Select your device/emulator
4. The app will install and launch automatically

## Troubleshooting

### Issue: Gradle Sync Failed

**Solution:**
- Check your internet connection
- Verify JDK is installed: `java -version` (should show Java 11+)
- In Android Studio: **File → Invalidate Caches / Restart → Invalidate and Restart**
- Try: **File → Sync Project with Gradle Files**

### Issue: SDK Not Found

**Solution:**
- Open **Tools → SDK Manager**
- Install Android SDK Platform 36
- Ensure `ANDROID_HOME` environment variable is set (optional but recommended)

### Issue: Build Errors Related to Dependencies

**Solution:**
- Clean the project: **Build → Clean Project**
- Rebuild: **Build → Rebuild Project**
- Delete `.gradle` folder and sync again

### Issue: Hilt/Kapt Errors

**Solution:**
- Ensure Kotlin version is 2.0.21 (check `gradle/libs.versions.toml`)
- Clean and rebuild: `./gradlew clean build`
- Invalidate caches and restart Android Studio

### Issue: Network/API Errors

**Solution:**
- Verify internet connection
- Check if Jikan API is accessible: https://api.jikan.moe/v4/top/anime
- The app works offline with cached data if previously loaded

## Project Files Overview

**Important files that ARE committed:**
- ✅ `gradle/wrapper/` - Gradle wrapper (allows building without installing Gradle)
- ✅ `gradle/libs.versions.toml` - Dependency versions
- ✅ `build.gradle.kts` - Build configuration
- ✅ `settings.gradle.kts` - Project settings
- ✅ `gradle.properties` - Gradle properties
- ✅ All source code in `app/src/`

**Important files that are NOT committed (in `.gitignore`):**
- ❌ `local.properties` - SDK path (auto-generated)
- ❌ `build/` - Build outputs
- ❌ `.gradle/` - Gradle cache
- ❌ `.idea/` - IDE settings
- ❌ `*.apk`, `*.aab` - Compiled apps

## Verifying Setup

After cloning, verify everything is set up correctly:

1. **Check Gradle Wrapper:**
   ```bash
   ./gradlew --version
   ```
   Should show Gradle 8.13

2. **Check Dependencies:**
   ```bash
   ./gradlew dependencies --configuration debugRuntimeClasspath
   ```
   Should list all dependencies without errors

3. **Build Test:**
   ```bash
   ./gradlew assembleDebug
   ```
   Should complete successfully and generate APK

4. **Run Tests:**
   ```bash
   ./gradlew test
   ```
   Should run unit tests (if any are implemented)

## Generating APK

To generate a debug APK:
```bash
./gradlew assembleDebug
```

The APK will be located at: `app/build/outputs/apk/debug/app-debug.apk`

## API Information

This app uses the [Jikan API](https://jikan.moe/) - an unofficial MyAnimeList API.

**Endpoints Used:**
- `GET /v4/top/anime` - Fetch top anime list
- `GET /v4/anime/{id}` - Fetch anime details
- `GET /v4/anime/{id}/characters` - Fetch character and voice actor information

**Rate Limits:**
- 3 requests per second
- 2 requests per second sustained
- 60 requests per minute

## Future Enhancements

- Implement infinite scrolling/pagination
- Implement search functionality
- Add favorites/bookmark feature
- Add "More Like This" recommendations (currently placeholder)
- Add unit and instrumented tests
- Implement exponential backoff for rate limit errors
- Add dark mode toggle (currently follows system theme)
- Add sharing functionality for anime details
- Consider implementing native YouTube Android Player API for even better trailer performance

## License

This project is created for the Seekho Android Developer Assignment.

