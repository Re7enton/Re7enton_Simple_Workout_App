package com.example.re7entonwearworkout.timer

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.wear.compose.material3.AppScaffold
import androidx.wear.compose.material3.CircularProgressIndicator
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.IconButton
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.OutlinedButton
import androidx.wear.compose.material3.Text
import androidx.wear.compose.material3.FilledTonalButton

@Composable
fun TimerScreen(
    vm: TimerViewModel = hiltViewModel(),
    onHydrationClick: () -> Unit
) {
    // Collect ViewModel state
    val remaining     by vm.remaining.collectAsState()
    val duration      by vm.duration.collectAsState()
    val isRunning     by vm.isRunning.collectAsState()
    val selectedPreset by vm.selectedPresetIndex.collectAsState()

    // Compute fraction
    val fraction = if (duration > 0) remaining / duration.toFloat() else 0f

    AppScaffold {
        Box(
            Modifier
                .fillMaxSize()
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            // Determinate ring using lambda
            CircularProgressIndicator(
                progress = { fraction.coerceIn(0f, 1f) },
                modifier = Modifier.fillMaxSize(),
                strokeWidth = 4.dp
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Countdown text
                Text(
                    text = "${remaining}s",
                    style = MaterialTheme.typography.displayLarge
                )

                // Preset buttons
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    PresetToggle("1 min", selectedPreset == 0) { vm.selectPreset(0) }
                    PresetToggle("2 min", selectedPreset == 1) { vm.selectPreset(1) }
                }

                // Controls: start/stop & hydration
                Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                    IconButton(onClick = { vm.toggleStartStop {} }) {
                        Icon(
                            imageVector = if (isRunning) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                            contentDescription = if (isRunning) "Stop" else "Start"
                        )
                    }
                    IconButton(onClick = onHydrationClick) {
                        Icon(
                            imageVector = Icons.Filled.LocalDrink,
                            contentDescription = "Hydration"
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PresetToggle(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    if (selected) {
        FilledTonalButton(onClick = onClick) {
            Text(label, style = MaterialTheme.typography.bodyLarge)
        }
    } else {
        OutlinedButton(onClick = onClick) {
            Text(label, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TimerScreenPreview() {
    TimerScreen(vm = TimerViewModel(), onHydrationClick = {})
}