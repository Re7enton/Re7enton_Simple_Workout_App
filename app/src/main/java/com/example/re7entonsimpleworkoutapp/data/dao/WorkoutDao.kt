package com.example.re7entonsimpleworkoutapp.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.re7entonsimpleworkoutapp.data.model.Workout
import com.example.re7entonsimpleworkoutapp.data.model.WorkoutSet
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao {
    @Query("SELECT * FROM workouts")
    fun getAllWorkouts(): Flow<List<Workout>>

    @Insert suspend fun insertWorkout(w: Workout): Long
    @Update
    suspend fun updateWorkout(w: Workout)
    @Delete suspend fun deleteWorkout(w: Workout)

    @Query("SELECT * FROM sets WHERE workoutId = :wid ORDER BY timestamp")
    fun getSetsForWorkout(wid: Long): Flow<List<WorkoutSet>>

    @Insert suspend fun insertSet(s: WorkoutSet)
    @Update suspend fun updateSet(s: WorkoutSet)
    @Delete suspend fun deleteSet(s: WorkoutSet)
}