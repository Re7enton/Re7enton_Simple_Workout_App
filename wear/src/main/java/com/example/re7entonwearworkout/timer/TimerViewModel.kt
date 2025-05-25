package com.example.re7entonwearworkout.timer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class TimerViewModel @Inject constructor(): ViewModel() {
    private val _duration = MutableStateFlow(60)      // default secs
    val duration: StateFlow<Int> = _duration.asStateFlow()

    private val _remaining = MutableStateFlow(0)
    val remaining: StateFlow<Int> = _remaining.asStateFlow()

    /** Set timer length. */
    fun setDuration(sec: Int){
        _duration.value = sec
        Timber.d("Timer duration set to $sec s")
    }

    /** Start countdown. */
    fun start(){
        viewModelScope.launch {
            _remaining.value = _duration.value
            while(_remaining.value > 0){
                delay(1_000)
                _remaining.value = _remaining.value - 1
            }
            Timber.d("Timer finished")
        }
    }
}