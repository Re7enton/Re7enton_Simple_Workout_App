package com.example.re7entonwearworkout.presentation.theme

import androidx.compose.runtime.Composable
import androidx.wear.compose.material3.MaterialTheme

/** A no-frills WearOS theme using the library’s defaults. */
@Composable
fun Re7entonWearWorkoutTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        // no custom colorScheme, typography, or shapes → use Wear’s defaults
        content = content
    )
}