package com.example.re7entonwearworkout.data

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import timber.log.Timber

class WaterActionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            ACTION_DRINK -> {
                // Enqueue a one‑time worker to increment the water count
                WorkManager.getInstance(context)
                    .enqueue(OneTimeWorkRequestBuilder<IncrementWorker>().build())
                Timber.d("Drink action received")
            }
            ACTION_SKIP -> {
                // Reschedule the next reminder in one hour
                HydrationRepository(context, WorkManager.getInstance(context))
                    .scheduleHourlyReminder()
                Timber.d("Skip action received")
            }
        }
        // Dismiss the notification
        NotificationManagerCompat.from(context).cancel(1001)
    }

    companion object {
        private const val REQUEST_DRINK = 0
        private const val REQUEST_SKIP  = 1

        const val ACTION_DRINK = "com.example.re7entonwearosworkout.ACTION_DRINK"
        const val ACTION_SKIP  = "com.example.re7entonwearosworkout.ACTION_SKIP"

        /** PendingIntent for the “Drink” action button. */
        fun pendingDrinkIntent(ctx: Context): PendingIntent {
            val intent = Intent(ctx, WaterActionReceiver::class.java)
                .setAction(ACTION_DRINK)
            return PendingIntent.getBroadcast(
                ctx,
                REQUEST_DRINK,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }

        /** PendingIntent for the “Skip” action button. */
        fun pendingSkipIntent(ctx: Context): PendingIntent {
            val intent = Intent(ctx, WaterActionReceiver::class.java)
                .setAction(ACTION_SKIP)
            return PendingIntent.getBroadcast(
                ctx,
                REQUEST_SKIP,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }
    }
}