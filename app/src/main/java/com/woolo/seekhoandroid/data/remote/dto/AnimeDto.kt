package com.woolo.seekhoandroid.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TopAnimeResponse(
    @Json(name = "data") val data: List<AnimeDto>,
    @Json(name = "pagination") val pagination: PaginationDto?
)

@JsonClass(generateAdapter = true)
data class AnimeDto(
    @Json(name = "mal_id") val malId: Int,
    @Json(name = "url") val url: String?,
    @Json(name = "images") val images: ImagesDto?,
    @Json(name = "trailer") val trailer: TrailerDto?,
    @Json(name = "title") val title: String?,
    @Json(name = "title_english") val titleEnglish: String?,
    @Json(name = "title_japanese") val titleJapanese: String?,
    @Json(name = "type") val type: String?,
    @Json(name = "source") val source: String?,
    @Json(name = "episodes") val episodes: Int?,
    @Json(name = "status") val status: String?,
    @Json(name = "airing") val airing: Boolean?,
    @Json(name = "aired") val aired: AiredDto?,
    @Json(name = "duration") val duration: String?,
    @Json(name = "rating") val rating: String?,
    @Json(name = "score") val score: Double?,
    @Json(name = "scored_by") val scoredBy: Int?,
    @Json(name = "rank") val rank: Int?,
    @Json(name = "popularity") val popularity: Int?,
    @Json(name = "members") val members: Int?,
    @Json(name = "favorites") val favorites: Int?,
    @Json(name = "synopsis") val synopsis: String?,
    @Json(name = "background") val background: String?,
    @Json(name = "season") val season: String?,
    @Json(name = "year") val year: Int?,
    @Json(name = "broadcast") val broadcast: BroadcastDto?,
    @Json(name = "producers") val producers: List<ProducerDto>?,
    @Json(name = "licensors") val licensors: List<LicensorDto>?,
    @Json(name = "studios") val studios: List<StudioDto>?,
    @Json(name = "genres") val genres: List<GenreDto>?,
    @Json(name = "explicit_genres") val explicitGenres: List<GenreDto>?,
    @Json(name = "themes") val themes: List<GenreDto>?,
    @Json(name = "demographics") val demographics: List<GenreDto>?
)

@JsonClass(generateAdapter = true)
data class ImagesDto(
    @Json(name = "jpg") val jpg: ImageUrlDto?,
    @Json(name = "webp") val webp: ImageUrlDto?
)

@JsonClass(generateAdapter = true)
data class ImageUrlDto(
    @Json(name = "image_url") val imageUrl: String?,
    @Json(name = "small_image_url") val smallImageUrl: String?,
    @Json(name = "large_image_url") val largeImageUrl: String?
)

@JsonClass(generateAdapter = true)
data class TrailerDto(
    @Json(name = "youtube_id") val youtubeId: String?,
    @Json(name = "url") val url: String?,
    @Json(name = "embed_url") val embedUrl: String?
)

@JsonClass(generateAdapter = true)
data class AiredDto(
    @Json(name = "from") val from: String?,
    @Json(name = "to") val to: String?,
    @Json(name = "prop") val prop: AiredPropDto?,
    @Json(name = "string") val string: String?
)

@JsonClass(generateAdapter = true)
data class AiredPropDto(
    @Json(name = "from") val from: DatePropDto?,
    @Json(name = "to") val to: DatePropDto?
)

@JsonClass(generateAdapter = true)
data class DatePropDto(
    @Json(name = "day") val day: Int?,
    @Json(name = "month") val month: Int?,
    @Json(name = "year") val year: Int?
)

@JsonClass(generateAdapter = true)
data class BroadcastDto(
    @Json(name = "day") val day: String?,
    @Json(name = "time") val time: String?,
    @Json(name = "timezone") val timezone: String?,
    @Json(name = "string") val string: String?
)

@JsonClass(generateAdapter = true)
data class ProducerDto(
    @Json(name = "mal_id") val malId: Int?,
    @Json(name = "type") val type: String?,
    @Json(name = "name") val name: String?,
    @Json(name = "url") val url: String?
)

@JsonClass(generateAdapter = true)
data class LicensorDto(
    @Json(name = "mal_id") val malId: Int?,
    @Json(name = "type") val type: String?,
    @Json(name = "name") val name: String?,
    @Json(name = "url") val url: String?
)

@JsonClass(generateAdapter = true)
data class StudioDto(
    @Json(name = "mal_id") val malId: Int?,
    @Json(name = "type") val type: String?,
    @Json(name = "name") val name: String?,
    @Json(name = "url") val url: String?
)

@JsonClass(generateAdapter = true)
data class GenreDto(
    @Json(name = "mal_id") val malId: Int?,
    @Json(name = "type") val type: String?,
    @Json(name = "name") val name: String?,
    @Json(name = "url") val url: String?
)

@JsonClass(generateAdapter = true)
data class PaginationDto(
    @Json(name = "last_visible_page") val lastVisiblePage: Int?,
    @Json(name = "has_next_page") val hasNextPage: Boolean?,
    @Json(name = "current_page") val currentPage: Int?,
    @Json(name = "items") val items: PaginationItemsDto?
)

@JsonClass(generateAdapter = true)
data class PaginationItemsDto(
    @Json(name = "count") val count: Int?,
    @Json(name = "total") val total: Int?,
    @Json(name = "per_page") val perPage: Int?
)

@JsonClass(generateAdapter = true)
data class AnimeDetailResponse(
    @Json(name = "data") val data: AnimeDto
)

