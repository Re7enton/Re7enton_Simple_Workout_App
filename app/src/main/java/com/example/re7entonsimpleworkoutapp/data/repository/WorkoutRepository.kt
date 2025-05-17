package com.example.re7entonsimpleworkoutapp.data.repository

import com.example.re7entonsimpleworkoutapp.data.dao.WorkoutDao
import com.example.re7entonsimpleworkoutapp.data.model.Workout
import com.example.re7entonsimpleworkoutapp.data.model.WorkoutSet
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkoutRepository @Inject constructor(
    private val dao: WorkoutDao
) {
    fun getWorkouts(): Flow<List<Workout>> = dao.getAllWorkouts()
    suspend fun addWorkout(name: String): Long = dao.insertWorkout(Workout(name = name))
    suspend fun updateWorkout(w: Workout) = dao.updateWorkout(w)
    suspend fun removeWorkout(w: Workout) = dao.deleteWorkout(w)

    fun getSets(id: Long): Flow<List<WorkoutSet>> = dao.getSetsForWorkout(id)
    suspend fun addSet(workoutId: Long, weight: Float) =
        dao.insertSet(WorkoutSet(workoutId = workoutId, weightKg = weight))
    suspend fun updateSet(s: WorkoutSet) = dao.updateSet(s)
    suspend fun removeSet(s: WorkoutSet) = dao.deleteSet(s)
}