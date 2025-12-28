package com.woolo.seekhoandroid.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.woolo.seekhoandroid.domain.usecase.SyncAnimeListUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import timber.log.Timber

@HiltWorker
class SyncAnimeWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val syncAnimeListUseCase: SyncAnimeListUseCase
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            Timber.d("Starting anime sync worker")
            syncAnimeListUseCase()
            Timber.d("Anime sync completed successfully")
            Result.success()
        } catch (e: Exception) {
            Timber.e(e, "Error in anime sync worker")
            Result.retry()
        }
    }
}

