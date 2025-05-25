package com.example.re7entonwearworkout.data

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.PendingIntentCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import timber.log.Timber

class WaterActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        val repo = HydrationRepository(context, WorkManager.getInstance(context))
        when(action) {
            ACTION_DRINK -> {
                // Increment count immediately
                WorkManager.getInstance(context).enqueue(
                    OneTimeWorkRequestBuilder<IncrementWorker>().build()
                )
                Timber.d("Drink action tapped")
            }
            ACTION_SKIP -> {
                // Reschedule another reminder in 1h
                repo.scheduleHourlyReminder()
                Timber.d("Skip action tapped")
            }
        }
        // Dismiss the notification
        NotificationManagerCompat.from(context).cancel(1001)
    }

    companion object {
        const val ACTION_DRINK = "com.example.re7entonwearosworkout.ACTION_DRINK"
        const val ACTION_SKIP  = "com.example.re7entonwearosworkout.ACTION_SKIP"

        fun pendingDrinkIntent(ctx: Context) = PendingIntentCompat.getBroadcast(
            ctx, 0,
            Intent(ctx, WaterActionReceiver::class.java).setAction(ACTION_DRINK),
            PendingIntentCompat.FLAG_UPDATE_CURRENT
        )

        fun pendingSkipIntent(ctx: Context) = PendingIntentCompat.getBroadcast(
            ctx, 1,
            Intent(ctx, WaterActionReceiver::class.java).setAction(ACTION_SKIP),
            PendingIntentCompat.FLAG_UPDATE_CURRENT
        )
    }
}