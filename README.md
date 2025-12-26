# Seekho Android - Anime Discovery App

A modern Android application built with Jetpack Compose that fetches and displays anime information from the Jikan API, with full offline support and robust error handling.

## Features Implemented

### Core Features
- ✅ **Anime List Screen**: Displays top-rated anime in a responsive grid layout
  - Shows anime title, poster image, number of episodes, and rating
  - Lazy loading with LazyVerticalGrid for optimal performance
  - Pull-to-refresh functionality
  - Loading states and error handling with retry mechanism

- ✅ **Anime Detail Screen**: Comprehensive anime information display
  - Video player for trailers (YouTube embed via WebView)
  - Fallback to poster image if trailer unavailable
  - Full synopsis, genres, studios, and additional metadata
  - Collapsible toolbar with smooth scrolling
  - All details: episodes, rating, status, type, source, duration, etc.

- ✅ **Offline Support**: Full offline functionality with Room database
  - Caches anime list and details locally
  - Automatic sync when network becomes available
  - Seamless transition between online/offline modes
  - Network connectivity observer with real-time updates

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
  - User-friendly error messages
  - Graceful fallback to cached data

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
2. **Trailer Playback**: Implemented YouTube trailer playback using WebView with embed URL, as ExoPlayer doesn't directly support YouTube URLs. For production, consider using YouTube Android Player API
3. **Pagination**: Currently fetches the first page of top anime. The API supports pagination, but infinite scrolling was not implemented to keep the scope manageable
4. **Main Cast**: The Jikan API's anime detail endpoint doesn't directly provide main cast information. To get character/voice actor data, you would need to call a separate endpoint (`/v4/anime/{id}/characters`), which was not implemented to keep the scope focused
5. **Image Loading**: Used Coil instead of Glide/Picasso as it's more Compose-friendly and performant
6. **Min SDK**: Set to 24 (Android 7.0) to support a wide range of devices while maintaining modern features
7. **Network Security**: Enabled `usesCleartextTraffic` for development. In production, this should be disabled and use HTTPS only
8. **Rate Limiting**: Jikan API has rate limits (3 requests/second, 2 requests/second after). The app includes delays and error handling, but doesn't implement exponential backoff retry logic

## Known Limitations

1. **Trailer Playback**: YouTube trailers are embedded via WebView. Some devices may have limitations with WebView video playback. For production, consider implementing YouTube Android Player API
2. **Pagination**: Only the first page of top anime is loaded. Infinite scrolling or pagination controls are not implemented
3. **Character/Cast Information**: Main cast information is not displayed as it requires a separate API call to `/v4/anime/{id}/characters` endpoint
4. **Image Caching**: While Coil handles image caching, there's no explicit offline image caching strategy beyond Coil's default behavior
5. **Sync Frequency**: Background sync runs every 4 hours. This is configurable but may not be optimal for all use cases
6. **Error Recovery**: While retry mechanisms are in place, there's no exponential backoff for API rate limit errors
7. **Testing**: Unit tests and instrumented tests are set up but not fully implemented. The project structure supports testing with JUnit, Mockito, and Turbine

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

## Building the Project

1. Clone the repository
2. Open in Android Studio (Hedgehog or later recommended)
3. Sync Gradle files
4. Build and run on an emulator or device

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

**Rate Limits:**
- 3 requests per second
- 2 requests per second sustained
- 60 requests per minute

## Future Enhancements

- Implement infinite scrolling/pagination
- Add character/cast information from separate API endpoint
- Implement search functionality
- Add favorites/bookmark feature
- Improve trailer playback with YouTube Player API
- Add unit and instrumented tests
- Implement exponential backoff for rate limit errors
- Add dark mode toggle (currently follows system theme)
- Add sharing functionality for anime details

## License

This project is created for the Seekho Android Developer Assignment.

