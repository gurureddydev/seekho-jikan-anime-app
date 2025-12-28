package com.woolo.seekhoandroid.domain.model

data class Character(
    val malId: Int,
    val name: String,
    val imageUrl: String?,
    val role: String?,
    val voiceActors: List<VoiceActor>
)

data class VoiceActor(
    val malId: Int,
    val name: String,
    val imageUrl: String?,
    val language: String?
)

