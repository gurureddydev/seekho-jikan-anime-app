package com.woolo.seekhoandroid.domain.model

data class Anime(
    val malId: Int,
    val title: String,
    val imageUrl: String?,
    val episodes: Int?,
    val score: Double?,
    val rank: Int?,
    val type: String? = null, // TV, Movie, OVA, ONA, Special, Music
    val genres: List<String>? = null // Genres for filtering
)

