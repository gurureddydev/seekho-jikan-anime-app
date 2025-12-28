package com.woolo.seekhoandroid.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.woolo.seekhoandroid.data.local.converter.GenreListConverter

@Entity(tableName = "anime")
@TypeConverters(GenreListConverter::class)
data class AnimeEntity(
    @PrimaryKey val malId: Int,
    val url: String?,
    val imageUrl: String?,
    val smallImageUrl: String?,
    val largeImageUrl: String?,
    val trailerYoutubeId: String?,
    val trailerUrl: String?,
    val trailerEmbedUrl: String?,
    val title: String?,
    val titleEnglish: String?,
    val titleJapanese: String?,
    val type: String?,
    val source: String?,
    val episodes: Int?,
    val status: String?,
    val airing: Boolean?,
    val duration: String?,
    val rating: String?,
    val score: Double?,
    val scoredBy: Int?,
    val rank: Int?,
    val popularity: Int?,
    val members: Int?,
    val favorites: Int?,
    val synopsis: String?,
    val background: String?,
    val season: String?,
    val year: Int?,
    val genres: List<String>?,
    val studios: List<String>?,
    val lastUpdated: Long = System.currentTimeMillis()
)

