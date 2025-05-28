package com.example.re7entonwearworkout.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.work.*
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.*
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

// Extension property to create DataStore<Preferences>
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "hydration_prefs")

@ViewModelScoped
class HydrationRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val workManager: WorkManager
) {
    // Preferences key
    private object Keys {
        val WATER_COUNT = intPreferencesKey("water_count")
    }

    // DataStore instance
    private val ds: DataStore<Preferences> = context.dataStore

    /** Flow of current water count; defaults to 0 if missing. */
    val waterCountFlow: Flow<Int> = ds.data
        .map { prefs -> prefs[Keys.WATER_COUNT] ?: 0 }
        .distinctUntilChanged()

    /** Increment the water count by 1. */
    suspend fun increment() {
        ds.edit { prefs ->
            val old = prefs[Keys.WATER_COUNT] ?: 0
            prefs[Keys.WATER_COUNT] = old + 1
            Timber.d("Hydration count incremented to ${old + 1}")
        }
    }

    /** Decrement by 1 but not below 0. */
    suspend fun decrement() {
        ds.edit { prefs ->
            val old = prefs[Keys.WATER_COUNT] ?: 0
            prefs[Keys.WATER_COUNT] = maxOf(0, old - 1)
            Timber.d("Hydration count decremented to ${prefs[Keys.WATER_COUNT]}")
        }
    }

    /** Reset the water count to zero. */
    suspend fun reset() {
        ds.edit { prefs ->
            prefs[Keys.WATER_COUNT] = 0
            Timber.d("Hydration count reset to 0")
        }
    }

    /** Schedule an hourly water‑drink reminder. */
    fun scheduleHourlyReminder() {
        val request = PeriodicWorkRequestBuilder<WaterReminderWorker>(1, TimeUnit.HOURS)
            .setConstraints(
                Constraints.Builder()
                    .setRequiresBatteryNotLow(true)
                    .build()
            )
            .build()

        workManager.enqueueUniquePeriodicWork(
            "water_reminder",
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
        Timber.d("Scheduled hourly water reminders")
    }

    /** Schedule a one‑time midnight reset of the water count. */
    fun scheduleMidnightReset() {
        val now = System.currentTimeMillis()
        // Calculate next midnight (in device local time)
        val midnight = ((now / 86_400_000L) + 1) * 86_400_000L
        val delay = midnight - now

        val request = OneTimeWorkRequestBuilder<ResetWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .build()

        workManager.enqueueUniqueWork(
            "midnight_reset",
            ExistingWorkPolicy.REPLACE,
            request
        )
        Timber.d("Scheduled midnight reset in ${delay / 1000}s")
    }
}