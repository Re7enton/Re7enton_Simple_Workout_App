package com.example.re7entonwearworkout


import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class WearApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Plant Timber in debug only
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        // Create notification channel for hydration reminders
        NotificationChannel(
            "hydration_channel",
            "Hydration Reminders",
            NotificationManager.IMPORTANCE_HIGH
        ).also { channel ->
            getSystemService(NotificationManager::class.java)
                .createNotificationChannel(channel)
        }
    }
}