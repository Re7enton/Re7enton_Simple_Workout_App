package com.example.re7entonwearworkout.timer

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue                  // <-- delegate import
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.Slider
import androidx.wear.compose.material3.Text
import androidx.wear.compose.material3.TimeText
import androidx.wear.compose.material3.AppScaffold
import androidx.compose.foundation.layout.fillMaxSize


@Composable
fun TimerScreen(vm: TimerViewModel = hiltViewModel()) {
    // 1) Collect state from the ViewModel
    val remaining by vm.remaining.collectAsState()
    val duration  by vm.duration.collectAsState()

    // 2) Use the Wear Material3 AppScaffold
    AppScaffold {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Show current device time
                TimeText()

                Spacer(Modifier.height(16.dp))

                // Countdown display
                Text(
                    text = "$remaining s",
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(Modifier.height(16.dp))

                // Start button
                Button(onClick = { vm.start() }) {
                    Text("Start")
                }

                Spacer(Modifier.height(8.dp))

                // Duration slider
                Text("Set timer:")
                Slider(
                    value = duration.toFloat(),
                    onValueChange = { newValue ->
                        vm.setDuration(newValue.toInt())
                    },
                    valueRange = 10f..300f,
                    steps = 28,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )
            }
        }
    }
}

@Composable
fun TimerScreenPreview() {
    // For preview, simply call without a real ViewModel
    TimerScreen(vm = TimerViewModel())
}