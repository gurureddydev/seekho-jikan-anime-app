package com.woolo.seekhoandroid.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CharactersResponse(
    @Json(name = "data") val data: List<CharacterDataDto>
)

@JsonClass(generateAdapter = true)
data class CharacterDataDto(
    @Json(name = "character") val character: CharacterDto,
    @Json(name = "role") val role: String?,
    @Json(name = "voice_actors") val voiceActors: List<VoiceActorDto>?
)

@JsonClass(generateAdapter = true)
data class CharacterDto(
    @Json(name = "mal_id") val malId: Int,
    @Json(name = "url") val url: String?,
    @Json(name = "images") val images: CharacterImagesDto?,
    @Json(name = "name") val name: String?
)

@JsonClass(generateAdapter = true)
data class CharacterImagesDto(
    @Json(name = "jpg") val jpg: ImageUrlDto?,
    @Json(name = "webp") val webp: ImageUrlDto?
)

@JsonClass(generateAdapter = true)
data class VoiceActorDto(
    @Json(name = "person") val person: PersonDto?,
    @Json(name = "language") val language: String?
)

@JsonClass(generateAdapter = true)
data class PersonDto(
    @Json(name = "mal_id") val malId: Int,
    @Json(name = "url") val url: String?,
    @Json(name = "images") val images: PersonImagesDto?,
    @Json(name = "name") val name: String?
)

@JsonClass(generateAdapter = true)
data class PersonImagesDto(
    @Json(name = "jpg") val jpg: ImageUrlDto?
)

