package com.woolo.seekhoandroid.util

import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.woolo.seekhoandroid.worker.SyncAnimeWorker
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncManager @Inject constructor(
    private val workManager: WorkManager
) {
    fun schedulePeriodicSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncWorkRequest = PeriodicWorkRequestBuilder<SyncAnimeWorker>(
            4, TimeUnit.HOURS
        )
            .setConstraints(constraints)
            .build()

        workManager.enqueueUniquePeriodicWork(
            "sync_anime_work",
            ExistingPeriodicWorkPolicy.KEEP,
            syncWorkRequest
        )
    }

    fun cancelSync() {
        workManager.cancelUniqueWork("sync_anime_work")
    }
}

