package com.example.re7entonwearworkout.data

import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.re7entonwearworkout.R
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import timber.log.Timber

class WaterReminderWorker @AssistedInject constructor(
    @Assisted private val ctx: Context,
    @Assisted workerParams: WorkerParameters
) : CoroutineWorker(ctx, workerParams) {

    override suspend fun doWork(): Result {
        // Build the notification with two actions: Drink and Skip
        val drinkIntent = WaterActionReceiver.pendingDrinkIntent(ctx)
        val skipIntent  = WaterActionReceiver.pendingSkipIntent(ctx)

        val notification = NotificationCompat.Builder(ctx, "hydration_channel")
            .setSmallIcon(R.drawable.ic_glass)                // your glass icon in res/drawable
            .setContentTitle(ctx.getString(R.string.drink))   // "Drink"
            .setContentText(ctx.getString(R.string.drink_reminder_text))
            .addAction(R.drawable.ic_glass, ctx.getString(R.string.drink), drinkIntent)
            .addAction(R.drawable.ic_skip,ctx.getString(R.string.skip), skipIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        NotificationManagerCompat.from(ctx).notify(1001, notification)
        Timber.d("Dispatched water reminder notification")
        return Result.success()
    }
}