package com.example.re7entonwearworkout.data

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.WorkManager
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import timber.log.Timber

class IncrementWorker @AssistedInject constructor(
    @Assisted private val ctx: Context,
    @Assisted workerParams: WorkerParameters
) : CoroutineWorker(ctx, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            // Increment the stored water count
            val repo = HydrationRepository(ctx, WorkManager.getInstance(ctx))
            repo.increment()
            Timber.d("IncrementWorker: water count incremented")
            Result.success()
        } catch (e: Exception) {
            Timber.e(e, "IncrementWorker: failed to increment water count")
            Result.failure()
        }
    }
}