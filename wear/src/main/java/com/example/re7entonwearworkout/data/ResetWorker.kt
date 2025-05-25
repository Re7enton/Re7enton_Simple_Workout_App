package com.example.re7entonwearworkout.data

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import timber.log.Timber

class ResetWorker @AssistedInject constructor(
    @Assisted private val ctx: Context,
    @Assisted params: WorkerParameters
): CoroutineWorker(ctx, params) {
    override suspend fun doWork(): Result {
        // Reset count at midnight
        HydrationRepository(ctx, WorkManager.getInstance(ctx)).reset()
        // Reschedule next midnight
        HydrationRepository(ctx, WorkManager.getInstance(ctx)).scheduleMidnightReset()
        Timber.d("Midnight reset executed")
        return Result.success()
    }
}