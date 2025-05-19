package com.example.re7entonsimpleworkoutapp.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import timber.log.Timber


/**
 * Dialog for editing both rest‑between‑sets and rest‑between‑workouts,
 * and choosing which one to use on “Start.”
 */
@Composable
fun RestTimerDialog(
    defaultSet: Int,
    defaultWorkout: Int,
    useWorkoutTimer: Boolean,   // current choice from ViewModel
    onSave: (setSecs: Int, workoutSecs: Int, useWorkout: Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    // 1) Initialize local state from passed‑in values
    var selectedOption by remember { mutableStateOf(if (useWorkoutTimer) "Workouts" else "Sets") }
    var setTimeText by remember { mutableStateOf(defaultSet.toString()) }
    var workoutTimeText by remember { mutableStateOf(defaultWorkout.toString()) }

    AlertDialog(
        onDismissRequest = {
            Timber.d("Timer settings dismissed")
            onDismiss()
        },
        title = { Text("Configure Rest Timer") },
        text = {
            Column {
                // Choice between the two timers
                Text("Choose timer to start:", style = MaterialTheme.typography.bodyLarge)
                Row(Modifier.padding(vertical = 8.dp)) {
                    RadioButton(
                        selected = selectedOption == "Sets",
                        onClick = { selectedOption = "Sets" }
                    )
                    Text("Between Sets", Modifier.padding(start = 4.dp))
                    Spacer(Modifier.width(16.dp))
                    RadioButton(
                        selected = selectedOption == "Workouts",
                        onClick = { selectedOption = "Workouts" }
                    )
                    Text("Between Workouts", Modifier.padding(start = 4.dp))
                }

                Spacer(Modifier.height(8.dp))

                // Editable field for whichever timer is selected
                if (selectedOption == "Sets") {
                    OutlinedTextField(
                        value = setTimeText,
                        onValueChange = { input ->
                            setTimeText = input.filter { it.isDigit() }
                        },
                        label = { Text("Between Sets (s)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                } else {
                    OutlinedTextField(
                        value = workoutTimeText,
                        onValueChange = { input ->
                            workoutTimeText = input.filter { it.isDigit() }
                        },
                        label = { Text("Between Workouts (s)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                // Parse values, with safe fallback
                val s = setTimeText.toIntOrNull() ?: defaultSet
                val w = workoutTimeText.toIntOrNull() ?: defaultWorkout
                val choice = (selectedOption == "Workouts")
                Timber.d("Saving timer: Sets=$s, Workouts=$w, useWorkout=$choice")
                onSave(s, w, choice)
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = {
                Timber.d("Timer settings cancelled")
                onDismiss()
            }) {
                Text("Cancel")
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun RestTimerDialogPreview() {
    RestTimerDialog(
        defaultSet = 60,
        defaultWorkout = 120,
        useWorkoutTimer = false,
        onSave = { _, _, _ -> },
        onDismiss = {}
    )
}