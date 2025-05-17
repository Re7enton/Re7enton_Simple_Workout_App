package com.example.re7entonsimpleworkoutapp.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.DialogProperties
import com.example.re7entonsimpleworkoutapp.R
import timber.log.Timber

/**
 * Dialog for adding or editing a workout name.
 * If [initial] is non-null, this is “edit” mode.
 */
@Composable
fun WorkoutDialog(
    title: String,
    initial: String? = null,           // null = add, non-null = edit
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var text by remember { mutableStateOf(initial ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            TextField(
                value = text,
                onValueChange = { text = it },
                placeholder = { Text("Workout name") },
                singleLine = true
            )
        },
        confirmButton = {
            TextButton(onClick = {
                if (text.isBlank()) {
                    Timber.w("Empty workout name")
                } else {
                    onConfirm(text.trim())
                }
            }) { Text("OK") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
        properties = DialogProperties()
    )
}

@Preview(showBackground = true)
@Composable
fun WorkoutDialogPreview() {
    WorkoutDialog("Add Workout", onConfirm = {}, onDismiss = {})
}