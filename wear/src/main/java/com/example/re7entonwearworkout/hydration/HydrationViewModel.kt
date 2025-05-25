package com.example.re7entonwearworkout.hydration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.re7entonwearworkout.data.HydrationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HydrationViewModel @Inject constructor(
    private val repo: HydrationRepository
): ViewModel() {
    val waterCount: StateFlow<Int> = repo.waterCountFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    init {
        // schedule reminders & midnight reset
        repo.scheduleHourlyReminder()
        repo.scheduleMidnightReset()
    }

    /** Called when user taps “Drink” */
    fun drink() = viewModelScope.launch {
        repo.increment()
        Timber.d("User drank water")
    }

    /** Called when user taps “Skip” */
    fun skip() {
        repo.scheduleHourlyReminder()
        Timber.d("User skipped. Next reminder in 1h")
    }
}