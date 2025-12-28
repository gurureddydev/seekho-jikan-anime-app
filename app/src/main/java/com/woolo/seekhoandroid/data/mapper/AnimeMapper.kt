package com.woolo.seekhoandroid.data.mapper

import com.woolo.seekhoandroid.data.local.entity.AnimeEntity
import com.woolo.seekhoandroid.data.remote.dto.AnimeDto
import com.woolo.seekhoandroid.domain.model.Anime
import com.woolo.seekhoandroid.domain.model.AnimeDetail

fun AnimeDto.toAnimeEntity(): AnimeEntity {
    return AnimeEntity(
        malId = malId,
        url = url,
        imageUrl = images?.jpg?.imageUrl ?: images?.webp?.imageUrl,
        smallImageUrl = images?.jpg?.smallImageUrl ?: images?.webp?.smallImageUrl,
        largeImageUrl = images?.jpg?.largeImageUrl ?: images?.webp?.largeImageUrl,
        trailerYoutubeId = trailer?.youtubeId,
        trailerUrl = trailer?.url,
        trailerEmbedUrl = trailer?.embedUrl,
        title = title,
        titleEnglish = titleEnglish,
        titleJapanese = titleJapanese,
        type = type,
        source = source,
        episodes = episodes,
        status = status,
        airing = airing,
        duration = duration,
        rating = rating,
        score = score,
        scoredBy = scoredBy,
        rank = rank,
        popularity = popularity,
        members = members,
        favorites = favorites,
        synopsis = synopsis,
        background = background,
        season = season,
        year = year,
        genres = genres?.map { it.name ?: "" }?.filter { it.isNotEmpty() },
        studios = studios?.map { it.name ?: "" }?.filter { it.isNotEmpty() }
    )
}

fun AnimeEntity.toAnime(): Anime {
    return Anime(
        malId = malId,
        title = title ?: titleEnglish ?: titleJapanese ?: "Unknown",
        imageUrl = imageUrl,
        episodes = episodes,
        score = score,
        rank = rank,
        type = type,
        genres = genres
    )
}

fun AnimeEntity.toAnimeDetail(): AnimeDetail {
    return AnimeDetail(
        malId = malId,
        title = title ?: titleEnglish ?: titleJapanese ?: "Unknown",
        titleEnglish = titleEnglish,
        titleJapanese = titleJapanese,
        imageUrl = imageUrl,
        largeImageUrl = largeImageUrl,
        trailerYoutubeId = trailerYoutubeId,
        trailerEmbedUrl = trailerEmbedUrl,
        synopsis = synopsis,
        genres = genres ?: emptyList(),
        studios = studios ?: emptyList(),
        episodes = episodes,
        score = score,
        scoredBy = scoredBy,
        status = status,
        type = type,
        source = source,
        duration = duration,
        rating = rating,
        airing = airing,
        season = season,
        year = year,
        background = background
    )
}

fun AnimeDto.toAnime(): Anime {
    return Anime(
        malId = malId,
        title = title ?: titleEnglish ?: titleJapanese ?: "Unknown",
        imageUrl = images?.jpg?.imageUrl ?: images?.webp?.imageUrl,
        episodes = episodes,
        score = score,
        rank = rank,
        type = type,
        genres = genres?.mapNotNull { it.name } ?: emptyList()
    )
}

fun AnimeDto.toAnimeDetail(): AnimeDetail {
    return AnimeDetail(
        malId = malId,
        title = title ?: titleEnglish ?: titleJapanese ?: "Unknown",
        titleEnglish = titleEnglish,
        titleJapanese = titleJapanese,
        imageUrl = images?.jpg?.imageUrl ?: images?.webp?.imageUrl,
        largeImageUrl = images?.jpg?.largeImageUrl ?: images?.webp?.largeImageUrl,
        trailerYoutubeId = trailer?.youtubeId,
        trailerEmbedUrl = trailer?.embedUrl,
        synopsis = synopsis,
        genres = genres?.mapNotNull { it.name } ?: emptyList(),
        studios = studios?.mapNotNull { it.name } ?: emptyList(),
        episodes = episodes,
        score = score,
        scoredBy = scoredBy,
        status = status,
        type = type,
        source = source,
        duration = duration,
        rating = rating,
        airing = airing,
        season = season,
        year = year,
        background = background
    )
}

