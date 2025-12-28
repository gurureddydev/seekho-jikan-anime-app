package com.woolo.seekhoandroid.domain.model

data class AnimeDetail(
    val malId: Int,
    val title: String,
    val titleEnglish: String?,
    val titleJapanese: String?,
    val imageUrl: String?,
    val largeImageUrl: String?,
    val trailerYoutubeId: String?,
    val trailerEmbedUrl: String?,
    val synopsis: String?,
    val genres: List<String>,
    val studios: List<String>,
    val episodes: Int?,
    val score: Double?,
    val scoredBy: Int?,
    val status: String?,
    val type: String?,
    val source: String?,
    val duration: String?,
    val rating: String?,
    val airing: Boolean?,
    val season: String?,
    val year: Int?,
    val background: String?
)

