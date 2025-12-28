package com.woolo.seekhoandroid.data.remote

import com.woolo.seekhoandroid.data.remote.dto.AnimeDetailResponse
import com.woolo.seekhoandroid.data.remote.dto.CharactersResponse
import com.woolo.seekhoandroid.data.remote.dto.TopAnimeResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface JikanApiService {
    @GET("v4/top/anime")
    suspend fun getTopAnime(): TopAnimeResponse

    @GET("v4/anime/{id}")
    suspend fun getAnimeById(@Path("id") id: Int): AnimeDetailResponse

    @GET("v4/anime/{id}/characters")
    suspend fun getAnimeCharacters(@Path("id") id: Int): CharactersResponse
}

