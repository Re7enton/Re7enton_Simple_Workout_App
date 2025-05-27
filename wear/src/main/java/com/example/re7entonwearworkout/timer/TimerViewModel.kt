package com.example.re7entonwearworkout.timer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class TimerViewModel @Inject constructor() : ViewModel() {
    // Preset durations in seconds
    private val presets = listOf(60, 120)

    // Which preset is selected index (0 = 1min, 1 = 2min)
    private val _selectedPresetIndex = MutableStateFlow(0)
    val selectedPresetIndex: StateFlow<Int> = _selectedPresetIndex.asStateFlow()

    // The actual duration used when starting (read-only)
    private val _duration = MutableStateFlow(presets.first())
    val duration: StateFlow<Int> = _duration.asStateFlow()

    // Remaining time in current countdown
    private val _remaining = MutableStateFlow(0)
    val remaining: StateFlow<Int> = _remaining.asStateFlow()

    // Are we currently running?
    private val _isRunning = MutableStateFlow(false)
    val isRunning: StateFlow<Boolean> = _isRunning.asStateFlow()

    private var timerJob: Job? = null

    /** User taps a preset (0 = 1m, 1 = 2m). */
    fun selectPreset(index: Int) {
        if (index in presets.indices) {
            _selectedPresetIndex.value = index
            Timber.d("Preset selected: ${presets[index]}s")
        }
    }

    /** Start or stop the countdown. */
    fun toggleStartStop(onFinish: () -> Unit) {
        if (_isRunning.value) {
            // Stop
            timerJob?.cancel()
            _isRunning.value = false
            Timber.d("Timer stopped")
        } else {
            // Start: apply preset to duration & remaining
            val sec = presets[_selectedPresetIndex.value]
            _duration.value = sec
            _remaining.value = sec
            _isRunning.value = true

            timerJob = viewModelScope.launch {
                while (_remaining.value > 0 && _isRunning.value) {
                    delay(1_000L)
                    _remaining.value -= 1
                }
                if (_remaining.value == 0 && _isRunning.value) {
                    Timber.d("Timer finished")
                    onFinish()
                }
                _isRunning.value = false
            }
            Timber.d("Timer started for ${sec}s")
        }
    }
}