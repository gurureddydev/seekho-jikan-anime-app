package com.woolo.seekhoandroid.data.repository

import com.woolo.seekhoandroid.data.local.dao.AnimeDao
import com.woolo.seekhoandroid.data.local.entity.AnimeEntity
import com.woolo.seekhoandroid.data.mapper.toAnime
import com.woolo.seekhoandroid.data.mapper.toAnimeDetail
import com.woolo.seekhoandroid.data.mapper.toAnimeEntity
import com.woolo.seekhoandroid.data.mapper.toCharacters
import com.woolo.seekhoandroid.data.remote.JikanApiService
import com.woolo.seekhoandroid.domain.model.Anime
import com.woolo.seekhoandroid.domain.model.AnimeDetail
import com.woolo.seekhoandroid.domain.model.Character
import com.woolo.seekhoandroid.util.NetworkConnectivityObserver
import com.woolo.seekhoandroid.util.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnimeRepository @Inject constructor(
    private val apiService: JikanApiService,
    private val animeDao: AnimeDao,
    private val networkConnectivityObserver: NetworkConnectivityObserver
) {
    fun getTopAnime(): Flow<Result<List<Anime>>> = flow {
        // Netflix-style: Load cached data first (immediately show cached data)
        var cachedEntities: List<AnimeEntity> = emptyList()
        var hasCachedData = false
        try {
            cachedEntities = animeDao.getAllAnime().first()
            if (cachedEntities.isNotEmpty()) {
                hasCachedData = true
                val cachedAnimeList = cachedEntities.map { it.toAnime() }
                // Emit cached data immediately - no loading state
                emit(Result.Success(cachedAnimeList))
                Timber.d("AnimeRepository: Emitted cached data (${cachedAnimeList.size} items)")
            } else {
                // Only show loading if no cached data exists
                emit(Result.Loading)
            }
        } catch (e: Exception) {
            Timber.e(e, "Error loading cached anime list")
            // If cache read fails, show loading
            emit(Result.Loading)
        }
        
        // Then refresh from network in background (if online)
        try {
            val isOnline = networkConnectivityObserver.isOnline.first()
            
            if (isOnline) {
                Timber.d("AnimeRepository: Refreshing from network...")
                // Fetch from API
                val response = apiService.getTopAnime()
                val animeList = response.data.map { it.toAnime() }
                
                // Save to database
                val entities = response.data.map { it.toAnimeEntity() }
                animeDao.insertAllAnime(entities)
                
                // Emit fresh data (this will update the UI if different from cache)
                emit(Result.Success(animeList))
                Timber.d("AnimeRepository: Emitted fresh data (${animeList.size} items)")
            } else {
                Timber.d("AnimeRepository: Offline, using cached data only")
                // If offline and no cache was emitted above, try one more time
                if (!hasCachedData) {
                    val entities = animeDao.getAllAnime().first()
                    if (entities.isEmpty()) {
                        emit(Result.Error(Exception("No internet connection and no cached data available")))
                    } else {
                        val animeList = entities.map { it.toAnime() }
                        emit(Result.Success(animeList))
                    }
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "Error fetching top anime from network")
            // If network fetch fails but we have cache, keep showing cache
            // Only emit error if we have no cached data
            if (!hasCachedData) {
                try {
                    val entities = animeDao.getAllAnime().first()
                    if (entities.isEmpty()) {
                        emit(Result.Error(e))
                    }
                    // If we have cache, don't emit error - just keep showing cache
                } catch (dbException: Exception) {
                    emit(Result.Error(e))
                }
            }
        }
    }

    fun getAnimeById(malId: Int): Flow<Result<AnimeDetail>> = flow {
        // Netflix-style: Load cached data first (immediately show cached data)
        var cachedEntity: AnimeEntity? = null
        try {
            cachedEntity = animeDao.getAnimeById(malId).first()
            if (cachedEntity != null) {
                val cachedAnimeDetail = cachedEntity.toAnimeDetail()
                // Emit cached data immediately - no loading state
                emit(Result.Success(cachedAnimeDetail))
                Timber.d("AnimeRepository: Emitted cached anime detail for malId=$malId")
            } else {
                // Only show loading if no cached data exists
                emit(Result.Loading)
            }
        } catch (e: Exception) {
            Timber.e(e, "Error loading cached anime detail")
            // If cache read fails, show loading
            emit(Result.Loading)
        }
        
        // Then refresh from network in background (if online)
        try {
            val isOnline = networkConnectivityObserver.isOnline.first()
            
            if (isOnline) {
                Timber.d("AnimeRepository: Refreshing anime detail from network for malId=$malId...")
                // Fetch from API
                val response = apiService.getAnimeById(malId)
                val animeDetail = response.data.toAnimeDetail()
                
                // Save to database
                val entity = response.data.toAnimeEntity()
                animeDao.insertAnime(entity)
                
                // Emit fresh data (this will update the UI if different from cache)
                emit(Result.Success(animeDetail))
                Timber.d("AnimeRepository: Emitted fresh anime detail for malId=$malId")
            } else {
                Timber.d("AnimeRepository: Offline, using cached anime detail only")
                // If offline and no cache was emitted above, try one more time
                if (cachedEntity == null) {
                    val entity = animeDao.getAnimeById(malId).first()
                    if (entity == null) {
                        emit(Result.Error(Exception("No internet connection and anime not cached")))
                    } else {
                        val animeDetail = entity.toAnimeDetail()
                        emit(Result.Success(animeDetail))
                    }
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "Error fetching anime detail from network")
            // If network fetch fails but we have cache, keep showing cache
            // Only emit error if we have no cached data
            try {
                val entity = animeDao.getAnimeById(malId).first()
                if (entity == null) {
                    emit(Result.Error(e))
                }
                // If we have cache, don't emit error - just keep showing cache
            } catch (dbException: Exception) {
                emit(Result.Error(e))
            }
        }
    }

    fun getAnimeCharacters(malId: Int): Flow<Result<List<Character>>> = flow {
        // Characters are not cached, so we always fetch from network
        try {
            val isOnline = networkConnectivityObserver.isOnline.first()
            
            if (isOnline) {
                emit(Result.Loading)
                Timber.d("AnimeRepository: Fetching characters for malId=$malId...")
                
                val response = apiService.getAnimeCharacters(malId)
                val characters = response.toCharacters()
                
                // Filter to show only main characters (role = "Main" or top characters)
                // Typically, main characters are at the top of the list
                val mainCharacters = characters
                    .filter { it.role?.equals("Main", ignoreCase = true) == true }
                    .take(10) // Limit to top 10 main characters
                    .ifEmpty { characters.take(10) } // If no "Main" role, take first 10
                
                emit(Result.Success(mainCharacters))
                Timber.d("AnimeRepository: Emitted ${mainCharacters.size} characters for malId=$malId")
            } else {
                emit(Result.Error(Exception("No internet connection")))
            }
        } catch (e: Exception) {
            Timber.e(e, "Error fetching characters for malId=$malId")
            emit(Result.Error(e))
        }
    }

    suspend fun syncAnimeList() {
        try {
            val isOnline = networkConnectivityObserver.isOnline.first()
            if (isOnline) {
                val response = apiService.getTopAnime()
                val entities = response.data.map { it.toAnimeEntity() }
                animeDao.insertAllAnime(entities)
            }
        } catch (e: Exception) {
            Timber.e(e, "Error syncing anime list")
        }
    }
}

