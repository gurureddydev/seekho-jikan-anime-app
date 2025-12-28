package com.woolo.seekhoandroid.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.woolo.seekhoandroid.data.local.converter.GenreListConverter
import com.woolo.seekhoandroid.data.local.dao.AnimeDao
import com.woolo.seekhoandroid.data.local.entity.AnimeEntity

@Database(
    entities = [AnimeEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(GenreListConverter::class)
abstract class AnimeDatabase : RoomDatabase() {
    abstract fun animeDao(): AnimeDao
}

