package com.example.re7entonsimpleworkoutapp.ui.components

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.DialogProperties
import timber.log.Timber

/**
 * Dialog for adding or editing a set weight.
 * [initial] in kg, null = add mode.
 */
@Composable
fun SetDialog(
    title: String,
    initial: Float? = null,
    onConfirm: (Float) -> Unit,
    onDismiss: () -> Unit
) {
    var text by remember { mutableStateOf(initial?.toString() ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            TextField(
                value = text,
                onValueChange = {
                    // Allow only digits and dot, at most one dot
                    val filtered = it.filter { c -> c.isDigit() || c == '.' }
                    text = if (filtered.count { c->c=='.' } <= 1) filtered else text
                },
                placeholder = { Text("Weight (kg)") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        },
        confirmButton = {
            TextButton(onClick = {
                text.toFloatOrNull()?.let {
                    onConfirm(it)
                } ?: Timber.w("Invalid weight input: '$text'")
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
fun SetDialogPreview() {
    SetDialog("Add Set", onConfirm={}, onDismiss={})
}