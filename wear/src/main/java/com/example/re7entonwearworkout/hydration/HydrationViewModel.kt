package com.example.re7entonwearworkout.hydration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.re7entonwearworkout.data.HydrationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HydrationViewModel @Inject constructor(
    private val repo: HydrationRepository
) : ViewModel() {
    // Current count, observed
    val waterCount: StateFlow<Int> = repo.waterCountFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)

    init {
        // Start your background reminders
        repo.scheduleHourlyReminder()
        repo.scheduleMidnightReset()
    }

    /** Called when user taps “Drink” */
    fun drink() = viewModelScope.launch {
        repo.increment()
        Timber.d("User drank water")
    }

    /** Undo one glass if mistake */
    fun undo() = viewModelScope.launch {
        repo.decrement()
        Timber.d("User undid one drink")
    }
}