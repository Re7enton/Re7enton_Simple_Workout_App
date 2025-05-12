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
import androidx.compose.ui.window.DialogProperties
import com.example.re7entonsimpleworkoutapp.R

/**
 * A reusable dialog with a single TextField.
 * - [label]: dialog title and text hint.
 * - [onAdd]: called with the entered text on “OK”.
 * - [onDismiss]: called when user cancels.
 */
@Composable
fun AddWorkoutDialog(
    label: String = stringResource(R.string.enter_text),              // default title
    onAdd: (String) -> Unit,                   // callback for OK
    onDismiss: () -> Unit                      // callback for Cancel
) {
    var text by remember { mutableStateOf("") } // local state for TextField

    AlertDialog(
        onDismissRequest = onDismiss,          // outside tap = dismiss
        title = { Text(text = label) },        // show the label
        text = {
            TextField(
                value = text,
                onValueChange = { text = it }, // update local state
                placeholder = { Text(label) }, // hint text
                singleLine = true
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onAdd(text)               // pass input back
                }
            ) {
                Text(stringResource(R.string.ok))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        },
        properties = DialogProperties()       // allow default behavior
    )
}