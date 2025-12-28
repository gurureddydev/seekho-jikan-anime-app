package com.woolo.seekhoandroid.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.woolo.seekhoandroid.data.local.entity.AnimeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AnimeDao {
    @Query("SELECT * FROM anime ORDER BY rank ASC")
    fun getAllAnime(): Flow<List<AnimeEntity>>

    @Query("SELECT * FROM anime WHERE malId = :malId LIMIT 1")
    fun getAnimeById(malId: Int): Flow<AnimeEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnime(anime: AnimeEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllAnime(animeList: List<AnimeEntity>)

    @Query("DELETE FROM anime")
    suspend fun deleteAllAnime()

    @Query("SELECT COUNT(*) FROM anime")
    suspend fun getAnimeCount(): Int
}

