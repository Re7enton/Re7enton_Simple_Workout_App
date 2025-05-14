package com.example.re7entonsimpleworkoutapp.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import timber.log.Timber

/**
 * A dialog that counts down from [seconds] to 0.
 * - [onFinish] is called when timer reaches zero or when "OK" is tapped.
 * - [onCancel] is called if user taps "Cancel".
 */
@Composable
fun RestCountdownDialog(
    seconds: Int,
    onFinish: () -> Unit,
    onCancel: () -> Unit
) {
    // Track remaining seconds in local state
    var remaining by remember { mutableStateOf(seconds) }

    // Launch a coroutine when this composable is first shown
    LaunchedEffect(seconds) {
        // Loop once per second until time is up
        while (remaining > 0) {
            delay(1_000L)
            remaining -= 1
            Timber.d("Countdown: $remaining seconds left")
        }
        // Notify that timer finished
        onFinish()
    }

    AlertDialog(
        onDismissRequest = { /* Prevent dismiss on outside tap */ },
        title = { Text("Rest Timer") },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                // Show how many seconds are left
                Text(
                    text = "$remaining s",
                    style = MaterialTheme.typography.displaySmall
                )
                Spacer(modifier = Modifier.height(16.dp))
                // New, non‑deprecated overload: progress as lambda
                val fraction = (remaining.toFloat() / seconds.toFloat()).coerceIn(0f, 1f)
                CircularProgressIndicator(
                    progress = { fraction },    // pass a lambda returning current fraction
                    modifier = Modifier.size(64.dp)
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                Timber.i("Rest countdown confirmed (OK)")
                onFinish()
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = {
                Timber.d("Rest countdown cancelled")
                onCancel()
            }) {
                Text("Cancel")
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun RestCountdownDialogPreview() {
    RestCountdownDialog(
        seconds = 10,
        onFinish = { /* no-op */ },
        onCancel = { /* no-op */ }
    )
}