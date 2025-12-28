package com.woolo.seekhoandroid.domain.usecase

import com.woolo.seekhoandroid.data.repository.AnimeRepository
import com.woolo.seekhoandroid.domain.model.Character
import com.woolo.seekhoandroid.util.Result
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAnimeCharactersUseCase @Inject constructor(
    private val repository: AnimeRepository
) {
    operator fun invoke(malId: Int): Flow<Result<List<Character>>> {
        return repository.getAnimeCharacters(malId)
    }
}

