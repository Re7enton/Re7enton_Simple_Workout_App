package com.example.re7entonsimpleworkoutapp.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.re7entonsimpleworkoutapp.data.dao.WorkoutDao
import com.example.re7entonsimpleworkoutapp.data.model.Workout
import com.example.re7entonsimpleworkoutapp.data.model.WorkoutSet

// List all entities here and bump version on schema changes
@Database(entities = [Workout::class, WorkoutSet::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun workoutDao(): WorkoutDao  // expose DAO
}