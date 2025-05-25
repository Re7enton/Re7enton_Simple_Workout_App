package com.example.re7entonwearworkout.di

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.preferencesDataStore
import androidx.work.WorkManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// extension property to create DataStore<Preferences>
private val Context.dataStore by preferencesDataStore(name = "hydration_prefs")

@Module
@InstallIn(SingletonComponent::class)
object WearAppModule {

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context) =
        context.dataStore

    @Provides
    @Singleton
    fun provideWorkManager(@ApplicationContext context: Context): WorkManager =
        WorkManager.getInstance(context)
}