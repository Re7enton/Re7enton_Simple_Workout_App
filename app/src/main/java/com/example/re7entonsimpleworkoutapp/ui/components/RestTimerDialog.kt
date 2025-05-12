package com.example.re7entonsimpleworkoutapp.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.DialogProperties

/**
 * Dialog to configure rest times.
 * - [defaultSet], [defaultWorkout]: initial values in seconds.
 * - [onSave]: returns two Ints when “Save” is clicked.
 */
@Composable
fun RestTimerDialog(
    defaultSet: Int,
    defaultWorkout: Int,
    onSave: (setSecs: Int, workoutSecs: Int) -> Unit,
    onDismiss: () -> Unit
) {
    // Local string states (we’ll parse to Int)
    var setTime by remember { mutableStateOf(defaultSet.toString()) }
    var workoutTime by remember { mutableStateOf(defaultWorkout.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Rest Timer Settings") },
        text = {
            Column {
                // Between-sets input
                OutlinedTextField(
                    value = setTime,
                    onValueChange = { new ->
                        // only allow digits
                        setTime = new.filter { it.isDigit() }
                    },
                    label = { Text("Between Sets (s)") },
                    singleLine = true
                )
                // Between-workouts input
                OutlinedTextField(
                    value = workoutTime,
                    onValueChange = { new ->
                        workoutTime = new.filter { it.isDigit() }
                    },
                    label = { Text("Between Workouts (s)") },
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                // parse or fallback to defaults
                val s = setTime.toIntOrNull() ?: defaultSet
                val w = workoutTime.toIntOrNull() ?: defaultWorkout
                onSave(s, w)
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        properties = DialogProperties()
    )
}