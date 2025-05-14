package com.example.re7entonsimpleworkoutapp.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.re7entonsimpleworkoutapp.ui.components.AddWorkoutDialog
import com.example.re7entonsimpleworkoutapp.ui.components.RestTimerDialog
import com.example.re7entonsimpleworkoutapp.ui.theme.Re7entonSimpleWorkoutAppTheme
import com.example.re7entonsimpleworkoutapp.viewmodel.WorkoutViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.re7entonsimpleworkoutapp.ui.components.RestCountdownDialog
import timber.log.Timber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    vm: WorkoutViewModel = hiltViewModel()
) {
    // 1) Collect StateFlows from ViewModel
    val workouts by vm.workouts.collectAsState()
    val selectedWorkout by vm.selectedWorkout.collectAsState()
    val sets by vm.sets.collectAsState()
    val restSets by vm.restBetweenSets.collectAsState()
    val restWorkouts by vm.restBetweenWorkouts.collectAsState()

    // 2) Local UI state: which dialogs to show
    var showAddWorkout by remember { mutableStateOf(false) }
    var showAddSet by remember { mutableStateOf(false) }
    var showTimerSettings by remember { mutableStateOf(false) }
    var showTimerCountdown by remember { mutableStateOf(false) }
    // Which timer duration to use for countdown
    var countdownSeconds by remember { mutableStateOf(0) }

    Scaffold(
        modifier = modifier,
        topBar = {
            CenterAlignedTopAppBar(title = { Text("Simple Workout Tracker") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                Timber.d("FAB clicked: showAddWorkout")
                showAddWorkout = true
            }) {
                Icon(Icons.Default.Add, contentDescription = "Add Workout")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            // --- Workouts Row ---
            LazyRow(modifier = Modifier.padding(8.dp)) {
                items(workouts) { workout ->
                    // Highlight card if it's selected
                    val isSelected = workout.id == selectedWorkout?.id
                    Card(
                        modifier = Modifier
                            .padding(4.dp)
                            .clip(MaterialTheme.shapes.medium)
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected)
                                    MaterialTheme.colorScheme.primary
                                else Color.Gray,
                                shape = MaterialTheme.shapes.medium
                            )
                            .clickable {
                                Timber.d("Selected workout: ${workout.name}")
                                vm.selectWorkout(workout)
                            }
                    ) {
                        Text(
                            text = workout.name,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }

            // --- Info Text ---
            Text(
                text = "Selected: ${selectedWorkout?.name ?: "None"}",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            Text(
                text = "Rest settings: $restSets s between sets, $restWorkouts s between workouts",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )

            // --- Sets List ---
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp)
            ) {
                items(sets) { set ->
                    Text(
                        text = "Weight: ${set.weightKg} kg",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }

            // --- Buttons Row ---
            Row(modifier = Modifier.padding(8.dp)) {
                Button(
                    onClick = {
                        Timber.d("Add Set button clicked")
                        showAddSet = true
                    },
                    enabled = selectedWorkout != null
                ) {
                    Text("Add Set")
                }
                Spacer(modifier = Modifier.width(16.dp))
                Button(onClick = {
                    Timber.d("Rest Timer settings clicked")
                    showTimerSettings = true
                }) {
                    Text("Timer Settings")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        // Start a countdown for set rest
                        countdownSeconds = restSets
                        showTimerCountdown = true
                        Timber.d("Started rest countdown for $countdownSeconds seconds")
                    },
                    enabled = true
                ) {
                    Text("Start Rest")
                }
            }
        }

        // --- DIALOGS ---
        if (showAddWorkout) {
            AddWorkoutDialog(
                label = "New Workout Name",
                onAdd = { name ->
                    if (name.isBlank()) {
                        Timber.w("Tried to add blank workout")
                    } else {
                        vm.addWorkout(name)
                    }
                    showAddWorkout = false
                },
                onDismiss = { showAddWorkout = false }
            )
        }

        if (showAddSet) {
            AddWorkoutDialog(
                label = "Enter Weight (kg)",
                onAdd = { input ->
                    val w = input.toFloatOrNull()
                    if (w == null) {
                        Timber.w("Invalid weight input: '$input'")
                    } else {
                        vm.addSet(w)
                    }
                    showAddSet = false
                },
                onDismiss = { showAddSet = false }
            )
        }

        if (showTimerSettings) {
            RestTimerDialog(
                defaultSet = restSets,
                defaultWorkout = restWorkouts,
                onSave = { setsSec, workSec ->
                    vm.updateRestBetweenSets(setsSec)
                    vm.updateRestBetweenWorkouts(workSec)
                    Timber.i("Rest settings updated: $setsSec / $workSec")
                    showTimerSettings = false
                },
                onDismiss = {
                    Timber.d("Timer settings dismissed")
                    showTimerSettings = false
                }
            )
        }

        if (showTimerCountdown) {
            RestCountdownDialog(
                seconds = countdownSeconds,
                onFinish = {
                    Timber.i("Rest countdown finished")
                    showTimerCountdown = false
                },
                onCancel = {
                    Timber.d("Rest countdown cancelled")
                    showTimerCountdown = false
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    MainScreen(modifier = Modifier.fillMaxSize())
}