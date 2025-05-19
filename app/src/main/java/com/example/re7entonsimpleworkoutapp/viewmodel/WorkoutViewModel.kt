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
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

// Opt‑in so we can use flatMapLatest without warnings
@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class WorkoutViewModel @Inject constructor(
    private val repo: WorkoutRepository
) : ViewModel() {

    // 1) Stream of all workouts from the database
    private val _workouts: StateFlow<List<Workout>> =
        repo.getWorkouts()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val workouts: StateFlow<List<Workout>> = _workouts

    // 2) Currently selected workout (or null)
    private val _selected = MutableStateFlow<Workout?>(null)
    val selectedWorkout: StateFlow<Workout?> = _selected.asStateFlow()

    // 3) Stream of sets for the selected workout
    private val _sets: StateFlow<List<WorkoutSet>> =
        _selected
            .flatMapLatest { workout ->
                if (workout != null) repo.getSets(workout.id)
                else flowOf(emptyList())
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val sets: StateFlow<List<WorkoutSet>> = _sets

    // 4) Rest timers
    private val _restSets = MutableStateFlow(60)
    val restBetweenSets: StateFlow<Int> = _restSets.asStateFlow()

    private val _restWorkouts = MutableStateFlow(120)
    val restBetweenWorkouts: StateFlow<Int> = _restWorkouts.asStateFlow()

    // 5) Which timer to use: false = sets, true = workouts
    private val _useWorkoutTimer = MutableStateFlow(false)
    val useWorkoutTimer: StateFlow<Boolean> = _useWorkoutTimer.asStateFlow()

    /** Toggle which timer the countdown uses. */
    fun toggleTimerChoice(useWorkout: Boolean) {
        _useWorkoutTimer.value = useWorkout
        Timber.d("Timer choice set to: ${if (useWorkout) "Workout" else "Set"}")
    }

    /** User tapped a workout → select it. */
    fun selectWorkout(workout: Workout) {
        _selected.value = workout
    }

    /**
     * Add a new workout to the database, then auto‑select it.
     * Uses Flow.first() to get the latest list and find the inserted item.
     */
    fun addWorkout(name: String) = viewModelScope.launch {
        try {
            // Insert and get new row ID
            val newId = repo.addWorkout(name)

            // Fetch current workouts list once
            val currentList = repo.getWorkouts().first()
            // Find the newly inserted workout by ID
            val newWorkout = currentList.firstOrNull { it.id == newId }

            if (newWorkout != null) {
                _selected.value = newWorkout
                Timber.d("Added & auto‑selected workout '${newWorkout.name}' (id=$newId)")
            } else {
                Timber.w("Workout added but not found in list: id=$newId")
            }
        } catch (e: Exception) {
            Timber.e(e, "Error adding workout")
        }
    }

    /**
     * Remove a workout and clear selection if it was the one removed.
     */
    fun deleteWorkout(workout: Workout) = viewModelScope.launch {
        try {
            repo.removeWorkout(workout)
            if (_selected.value?.id == workout.id) {
                _selected.value = null
                Timber.d("Deleted selected workout '${workout.name}' and cleared selection")
            }
        } catch (e: Exception) {
            Timber.e(e, "Error deleting workout")
        }
    }

    /** Add a set to the currently selected workout. */
    fun addSet(weight: Float) = viewModelScope.launch {
        val w = _selected.value
        if (w == null) {
            Timber.w("Cannot add set: no workout selected")
            return@launch
        }
        try {
            repo.addSet(w.id, weight)
            Timber.d("Added set of $weight kg to workout '${w.name}'")
        } catch (e: Exception) {
            Timber.e(e, "Error adding set")
        }
    }

    /** Edit an existing workout’s name. */
    fun editWorkout(workout: Workout) = viewModelScope.launch {
        try {
            repo.updateWorkout(workout)
            Timber.d("Updated workout '${workout.name}' (id=${workout.id})")
        } catch (e: Exception) {
            Timber.e(e, "Error editing workout")
        }
    }

    /** Edit an existing set’s weight. */
    fun editSet(set: WorkoutSet) = viewModelScope.launch {
        try {
            repo.updateSet(set)
            Timber.d("Updated set id=${set.id} to ${set.weightKg} kg")
        } catch (e: Exception) {
            Timber.e(e, "Error editing set")
        }
    }

    /** Delete a set entry. */
    fun deleteSet(set: WorkoutSet) = viewModelScope.launch {
        try {
            repo.removeSet(set)
            Timber.d("Deleted set id=${set.id}")
        } catch (e: Exception) {
            Timber.e(e, "Error deleting set")
        }
    }

    /** Update the “between‑sets” timer. */
    fun updateRestBetweenSets(seconds: Int) {
        _restSets.value = seconds
    }

    /** Update the “between‑workouts” timer. */
    fun updateRestBetweenWorkouts(seconds: Int) {
        _restWorkouts.value = seconds
    }
}