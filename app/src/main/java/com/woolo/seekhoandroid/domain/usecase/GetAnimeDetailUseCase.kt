package com.woolo.seekhoandroid.domain.usecase

import com.woolo.seekhoandroid.data.repository.AnimeRepository
import com.woolo.seekhoandroid.domain.model.AnimeDetail
import com.woolo.seekhoandroid.util.Result
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAnimeDetailUseCase @Inject constructor(
    private val repository: AnimeRepository
) {
    operator fun invoke(malId: Int): Flow<Result<AnimeDetail>> {
        return repository.getAnimeById(malId)
    }
}

