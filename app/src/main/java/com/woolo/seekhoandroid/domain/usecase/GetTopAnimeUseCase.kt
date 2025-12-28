package com.woolo.seekhoandroid.domain.usecase

import com.woolo.seekhoandroid.data.repository.AnimeRepository
import com.woolo.seekhoandroid.domain.model.Anime
import com.woolo.seekhoandroid.util.Result
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTopAnimeUseCase @Inject constructor(
    private val repository: AnimeRepository
) {
    operator fun invoke(): Flow<Result<List<Anime>>> {
        return repository.getTopAnime()
    }
}

