package com.example.re7entonwearworkout.hydration

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.wear.compose.material3.AppScaffold
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.Text
import androidx.wear.compose.material3.TimeText

/**
 * Core Hydration UI, parameterized so Preview doesn't need a ViewModel.
 */
@Composable
fun HydrationScreen(
    count: Int,
    onDrink: () -> Unit,
    onSkip: () -> Unit,
    onBack: () -> Unit
) {
    AppScaffold{
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(Modifier.height(16.dp))
                Icon(
                    imageVector = Icons.Filled.LocalDrink,
                    contentDescription = "Drink water",
                    modifier = Modifier
                        .size(80.dp)
                        .clickable(onClick = onDrink)
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "You have drunk $count glasses",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(onClick = onDrink) { Text("Drink") }
                    Button(onClick = onSkip)  { Text("Skip")  }
                }
                Spacer(Modifier.height(8.dp))
                Button(onClick = onBack) { Text("Back") }
            }
        }
    }
}

/**
 * Real‑world overload that pulls count from the ViewModel.
 */
@Composable
fun HydrationScreen(
    vm: HydrationViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val count by vm.waterCount.collectAsState()
    HydrationScreen(
        count = count,
        onDrink = { vm.drink() },
        onSkip  = { vm.skip()  },
        onBack  = onBack
    )
}

@Preview(showBackground = true)
@Composable
fun HydrationScreenPreview() {
    // Preview with an arbitrary count
    HydrationScreen(
        count = 3,
        onDrink = {},
        onSkip = {},
        onBack = {}
    )
}