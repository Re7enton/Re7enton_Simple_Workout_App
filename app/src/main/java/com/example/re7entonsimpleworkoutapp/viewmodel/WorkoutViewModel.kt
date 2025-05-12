package com.example.re7entonsimpleworkoutapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.re7entonsimpleworkoutapp.data.model.Workout
import com.example.re7entonsimpleworkoutapp.data.model.WorkoutSet
import com.example.re7entonsimpleworkoutapp.data.repository.WorkoutRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class WorkoutViewModel @Inject constructor(
    private val repo: WorkoutRepository
) : ViewModel() {

    // 1) Expose all workouts as a StateFlow with an initial empty list
    private val _workouts = repo.getWorkouts()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )
    val workouts: StateFlow<List<Workout>> = _workouts

    // 2) Track which workout is selected; starts as null (no selection)
    private val _selectedWorkout = MutableStateFlow<Workout?>(null)
    val selectedWorkout: StateFlow<Workout?> = _selectedWorkout.asStateFlow()

    // 3) Whenever selectedWorkout changes, switch to its sets or empty list
    @OptIn(ExperimentalCoroutinesApi::class)
    private val _sets = _selectedWorkout
        .flatMapLatest { workout ->
            if (workout != null) {
                repo.getSets(workout.id)
            } else {
                flowOf(emptyList())
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )
    val sets: StateFlow<List<WorkoutSet>> = _sets

    // 4) Rest timers as StateFlows so UI can collect & display them
    private val _restBetweenSets = MutableStateFlow(60)
    val restBetweenSets: StateFlow<Int> = _restBetweenSets.asStateFlow()

    private val _restBetweenWorkouts = MutableStateFlow(120)
    val restBetweenWorkouts: StateFlow<Int> = _restBetweenWorkouts.asStateFlow()

    /** Call when user taps on a workout card to view/add sets. */
    fun selectWorkout(workout: Workout) {
        _selectedWorkout.value = workout
    }

    /** Add a new workout with [name]. */
    fun addWorkout(name: String) = viewModelScope.launch {
        try {
            repo.addWorkout(name)
        } catch (e: Exception) {
            Timber.e(e, "Error adding workout")
        }
    }

    /** Remove an existing workout (and its sets). */
    fun removeWorkout(workout: Workout) = viewModelScope.launch {
        try {
            repo.removeWorkout(workout)
            // If the removed workout was selected, clear selection
            if (_selectedWorkout.value?.id == workout.id) {
                _selectedWorkout.value = null
            }
        } catch (e: Exception) {
            Timber.e(e, "Error removing workout")
        }
    }

    /** Add a set with [weight] kg to the currently selected workout. */
    fun addSet(weight: Float) = viewModelScope.launch {
        val workout = _selectedWorkout.value
        if (workout == null) {
            Timber.w("Tried to add set but no workout is selected")
            return@launch
        }
        try {
            repo.addSet(workout.id, weight)
        } catch (e: Exception) {
            Timber.e(e, "Error adding set")
        }
    }

    /** Remove a previously added set. */
    fun removeSet(set: WorkoutSet) = viewModelScope.launch {
        try {
            repo.removeSet(set)
        } catch (e: Exception) {
            Timber.e(e, "Error removing set")
        }
    }

    /** Update the rest-between-sets timer. */
    fun updateRestBetweenSets(seconds: Int) {
        _restBetweenSets.value = seconds
    }

    /** Update the rest-between-workouts timer. */
    fun updateRestBetweenWorkouts(seconds: Int) {
        _restBetweenWorkouts.value = seconds
    }
}