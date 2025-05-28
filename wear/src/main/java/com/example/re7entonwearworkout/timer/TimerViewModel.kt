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
    private val presets = listOf(60, 120)           // available durations
    private val _selectedPresetIndex = MutableStateFlow(0)
    val selectedPresetIndex: StateFlow<Int> = _selectedPresetIndex.asStateFlow()

    private val _duration = MutableStateFlow(presets.first())
    val duration: StateFlow<Int> = _duration.asStateFlow()

    private val _remaining = MutableStateFlow(0)
    val remaining: StateFlow<Int> = _remaining.asStateFlow()

    private val _isRunning = MutableStateFlow(false)
    val isRunning: StateFlow<Boolean> = _isRunning.asStateFlow()

    private var timerJob: Job? = null

    /** User picks 1 min or 2 min preset. */
    fun selectPreset(index: Int) {
        if (index in presets.indices) {
            _selectedPresetIndex.value = index
            Timber.d("Preset selected: ${presets[index]}s")
        }
    }

    /** Toggle ▶️ start / ■ stop. */
    fun toggleStartStop(onFinish: () -> Unit) {
        if (_isRunning.value) {
            // ■ STOP → cancel and reset
            timerJob?.cancel()
            _isRunning.value = false
            _remaining.value = 0
            Timber.d("Timer stopped and reset")
        } else {
            // ▶️ START → set duration and kick off countdown
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
                    onFinish()  // ⚡️ notify “we’re done”
                }
                _isRunning.value = false
            }
            Timber.d("Timer started for ${sec}s")
        }
    }
}