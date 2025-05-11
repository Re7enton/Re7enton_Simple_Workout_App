package com.example.re7entonsimpleworkoutapp.di

import android.content.Context
import androidx.room.Room
import com.example.re7entonsimpleworkoutapp.data.dao.WorkoutDao
import com.example.re7entonsimpleworkoutapp.data.db.AppDatabase
import com.example.re7entonsimpleworkoutapp.data.repository.WorkoutRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)  // lives as long as the app does
object AppModule {

    // Provide the Room database
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext ctx: Context): AppDatabase =
        Room.databaseBuilder(ctx, AppDatabase::class.java, "workouts.db")
            .fallbackToDestructiveMigration(dropAllTables = true)  // wipe & rebuild if schema changes
            .build()

    // Provide the DAO from the database
    @Provides
    fun provideWorkoutDao(db: AppDatabase): WorkoutDao =
        db.workoutDao()

    // Provide a single repository instance
    @Provides
    @Singleton
    fun provideRepository(dao: WorkoutDao): WorkoutRepository =
        WorkoutRepository(dao)
}