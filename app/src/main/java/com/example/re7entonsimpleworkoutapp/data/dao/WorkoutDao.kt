package com.example.re7entonsimpleworkoutapp.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.re7entonsimpleworkoutapp.data.model.Workout
import com.example.re7entonsimpleworkoutapp.data.model.WorkoutSet
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao {
    // Get all workouts as a Flow to observe changes
    @Query("SELECT * FROM workouts")
    fun getAllWorkouts(): Flow<List<Workout>>

    // Insert a new workout, return its new ID
    @Insert
    suspend fun insertWorkout(w: Workout): Long

    // Delete a workout (and cascade-deletes its sets)
    @Delete
    suspend fun deleteWorkout(w: Workout)

    // Get all sets for a given workout, ordered by time
    @Query("SELECT * FROM sets WHERE workoutId = :wid ORDER BY timestamp")
    fun getSetsForWorkout(wid: Long): Flow<List<WorkoutSet>>

    // Insert a new set
    @Insert
    suspend fun insertSet(s: WorkoutSet)

    // Delete a set
    @Delete
    suspend fun deleteSet(s: WorkoutSet)
}