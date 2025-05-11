package com.example.re7entonsimpleworkoutapp.data.repository

import com.example.re7entonsimpleworkoutapp.data.dao.WorkoutDao
import com.example.re7entonsimpleworkoutapp.data.model.Workout
import com.example.re7entonsimpleworkoutapp.data.model.WorkoutSet
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class WorkoutRepository @Inject constructor(
    private val dao: WorkoutDao
) {
    // Expose flows directly to ViewModel
    fun getWorkouts(): Flow<List<Workout>> = dao.getAllWorkouts()

    // Add a new workout
    suspend fun addWorkout(name: String): Long =
        dao.insertWorkout(Workout(name = name))

    // Remove a workout
    suspend fun removeWorkout(w: Workout) =
        dao.deleteWorkout(w)

    // Get sets for one workout
    fun getSets(workoutId: Long): Flow<List<WorkoutSet>> =
        dao.getSetsForWorkout(workoutId)

    // Add a new set entry
    suspend fun addSet(workoutId: Long, weight: Float) =
        dao.insertSet(WorkoutSet(workoutId = workoutId, weightKg = weight))

    // Remove a set entry
    suspend fun removeSet(s: WorkoutSet) =
        dao.deleteSet(s)
}