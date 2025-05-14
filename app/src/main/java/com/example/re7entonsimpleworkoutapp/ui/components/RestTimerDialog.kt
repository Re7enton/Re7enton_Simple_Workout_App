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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.DialogProperties
import timber.log.Timber

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
    // Local text state for the two inputs
    var setTime by remember { mutableStateOf(defaultSet.toString()) }
    var workoutTime by remember { mutableStateOf(defaultWorkout.toString()) }

    AlertDialog(
        onDismissRequest = {
            Timber.d("RestTimerDialog dismissed by outside tap")
            onDismiss()
        },
        title = { Text("Rest Timer Settings") },
        text = {
            Column {
                OutlinedTextField(
                    value = setTime,
                    onValueChange = { new ->
                        // Only digits allowed
                        setTime = new.filter { it.isDigit() }
                    },
                    label = { Text("Between Sets (s)") },
                    singleLine = true
                )
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
                val s = setTime.toIntOrNull() ?: defaultSet
                val w = workoutTime.toIntOrNull() ?: defaultWorkout
                Timber.d("RestTimerDialog saving: $s s / $w s")
                onSave(s, w)
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = {
                Timber.d("RestTimerDialog Cancel clicked")
                onDismiss()
            }) {
                Text("Cancel")
            }
        },
        properties = DialogProperties()
    )
}

@Preview(showBackground = true)
@Composable
fun RestTimerDialogPreview() {
    RestTimerDialog(
        defaultSet = 60,
        defaultWorkout = 120,
        onSave = { _, _ -> },
        onDismiss = {}
    )
}