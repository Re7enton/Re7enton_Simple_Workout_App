package com.example.re7entonwearworkout.hydration

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Undo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.wear.compose.material3.*

/**
 * Core Hydration UI, parameterized so Preview doesn't need a ViewModel.
 */
@Composable
fun HydrationScreen(
    count: Int,
    onDrink: () -> Unit,
    onUndo: () -> Unit,
    onBack: () -> Unit
) {
    AppScaffold {
        Box(
            Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Big colored water icon; tap to drink
                Icon(
                    imageVector = Icons.Default.LocalDrink,
                    contentDescription = "Drink water",
                    modifier = Modifier
                        .size(80.dp)
                        .clickable(onClick = onDrink),
                    tint = Color(0xFF42A5F5)
                )

                // Display count of glasses
                Text(
                    text = "You've had $count glasses",
                    style = MaterialTheme.typography.bodyLarge
                )

                // Undo last drink button
                IconButton(onClick = onUndo) {
                    Icon(
                        imageVector = Icons.Default.Restore,
                        contentDescription = "Undo last drink"
                    )
                }

                // Back to timer button
                Button(onClick = onBack) {
                    Text("Back")
                }
            }
        }
    }
}

/**
 * Runtime overload that pulls count from the ViewModel.
 */
@Composable
fun HydrationScreen(
    vm: HydrationViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val count by vm.waterCount.collectAsState()
    HydrationScreen(
        count    = count,
        onDrink  = { vm.drink() },
        onUndo   = { vm.undo() },
        onBack   = onBack
    )
}

/** Preview using the parameterized variant—no ViewModel needed. */
@Preview(showBackground = true)
@Composable
fun HydrationScreenPreview() {
    HydrationScreen(
        count  = 3,
        onDrink = {},
        onUndo  = {},
        onBack  = {}
    )
}