package com.woolo.seekhoandroid.domain.usecase

import com.woolo.seekhoandroid.data.repository.AnimeRepository
import javax.inject.Inject

class SyncAnimeListUseCase @Inject constructor(
    private val repository: AnimeRepository
) {
    suspend operator fun invoke() {
        repository.syncAnimeList()
    }
}

