package com.example.re7entonsimpleworkoutapp.ui.screens

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.re7entonsimpleworkoutapp.ui.components.AddWorkoutDialog
import com.example.re7entonsimpleworkoutapp.ui.components.RestTimerDialog
import com.example.re7entonsimpleworkoutapp.ui.theme.Re7entonSimpleWorkoutAppTheme
import com.example.re7entonsimpleworkoutapp.viewmodel.WorkoutViewModel
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    vm: WorkoutViewModel = hiltViewModel()
) {
    // 1) Collect all StateFlows as Compose State
    val workouts by vm.workouts.collectAsState()
    val selectedWorkout by vm.selectedWorkout.collectAsState()
    val sets by vm.sets.collectAsState()
    val restSets by vm.restBetweenSets.collectAsState()
    val restWorkouts by vm.restBetweenWorkouts.collectAsState()

    // 2) Local state for showing/hiding dialogs
    var showAddWorkout by remember { mutableStateOf(false) }
    var showAddSet by remember { mutableStateOf(false) }
    var showTimer by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("Simple Workout Tracker") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddWorkout = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Workout")
            }
        }
    ) { padding ->
        Column(modifier = Modifier
            .padding(padding)
            .fillMaxSize()
        ) {
            // Horizontal list of workouts
            LazyRow(modifier = Modifier.padding(8.dp)) {
                items(workouts) { workout ->
                    Card(
                        modifier = Modifier
                            .padding(4.dp)
                            .clickable { vm.selectWorkout(workout) }
                    ) {
                        Text(
                            text = workout.name,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }

            // Selected workout name and rest timers display
            Text(
                text = "Selected: ${selectedWorkout?.name ?: "None"}",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(8.dp)
            )
            Text(
                text = "Rest: $restSets s between sets, $restWorkouts s between workouts",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            // List of sets for the selected workout
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp)
            ) {
                items(sets) { set ->
                    Text(
                        text = "Weight: ${set.weightKg} kg",
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }

            // Buttons row
            Row(modifier = Modifier.padding(8.dp)) {
                Button(
                    onClick = { showAddSet = true },
                    enabled = selectedWorkout != null
                ) {
                    Text("Add Set")
                }
                Spacer(modifier = Modifier.width(16.dp))
                Button(onClick = { showTimer = true }) {
                    Text("Rest Timer")
                }
            }
        }

        // --- DIALOGS ---
        if (showAddWorkout) {
            AddWorkoutDialog(
                label = "New Workout Name",
                onAdd = { name ->
                    vm.addWorkout(name)
                    showAddWorkout = false
                },
                onDismiss = { showAddWorkout = false }
            )
        }
        if (showAddSet) {
            AddWorkoutDialog(
                label = "Enter Weight (kg)",
                onAdd = { input ->
                    input.toFloatOrNull()?.let { vm.addSet(it) }
                    showAddSet = false
                },
                onDismiss = { showAddSet = false }
            )
        }
        if (showTimer) {
            RestTimerDialog(
                defaultSet = restSets,
                defaultWorkout = restWorkouts,
                onSave = { setsSec, workSec ->
                    vm.updateRestBetweenSets(setsSec)
                    vm.updateRestBetweenWorkouts(workSec)
                    showTimer = false
                },
                onDismiss = { showTimer = false }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    Re7entonSimpleWorkoutAppTheme {
        MainScreen()
    }
}