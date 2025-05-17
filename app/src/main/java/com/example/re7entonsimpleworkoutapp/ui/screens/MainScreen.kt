package com.example.re7entonsimpleworkoutapp.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.re7entonsimpleworkoutapp.data.model.WorkoutSet
import com.example.re7entonsimpleworkoutapp.ui.components.RestCountdownDialog
import com.example.re7entonsimpleworkoutapp.ui.components.RestTimerDialog
import com.example.re7entonsimpleworkoutapp.ui.components.SetDialog
import com.example.re7entonsimpleworkoutapp.ui.components.WorkoutDialog
import com.example.re7entonsimpleworkoutapp.viewmodel.WorkoutViewModel
import timber.log.Timber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(vm: WorkoutViewModel = hiltViewModel()) {
    // 1) State from ViewModel
    val workouts by vm.workouts.collectAsState()
    val selected by vm.selectedWorkout.collectAsState()
    val sets by vm.sets.collectAsState()
    val restSets by vm.restBetweenSets.collectAsState()
    val restWorkouts by vm.restBetweenWorkouts.collectAsState()
    val useWorkoutTimer by vm.useWorkoutTimer.collectAsState()

    // 2) UI state
    var dropdownExpanded by remember { mutableStateOf(false) }
    var workoutDialog by remember { mutableStateOf<Pair<Boolean, String?>>(false to null) }
    var setDialog by remember { mutableStateOf<Pair<Boolean, WorkoutSet?>>(false to null) }
    var showTimerSettings by remember { mutableStateOf(false) }
    var showCountdown by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    ExposedDropdownMenuBox(
                        expanded = dropdownExpanded,
                        onExpandedChange = { dropdownExpanded = !dropdownExpanded }
                    ) {
                        Text(
                            text = selected?.name ?: "Select Workout",
                            modifier = Modifier
                                .clickable { dropdownExpanded = true }
                                .padding(8.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = dropdownExpanded,
                            onDismissRequest = { dropdownExpanded = false }
                        ) {
                            workouts.forEach { w ->
                                DropdownMenuItem(
                                    text = { Text(w.name) },
                                    onClick = {
                                        vm.selectWorkout(w)
                                        dropdownExpanded = false
                                        Timber.d("Selected workout: ${w.name}")
                                    },
                                    trailingIcon = {
                                        IconButton(onClick = {
                                            workoutDialog = true to w.name
                                        }) { Icon(Icons.Default.Edit, null) }
                                        IconButton(onClick = { vm.deleteWorkout(w) }) {
                                            Icon(Icons.Default.Delete, null)
                                        }
                                    }
                                )
                            }
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { workoutDialog = true to null }) {
                        Icon(Icons.Default.Add, null)
                    }
                }
            )
        },
        floatingActionButton = {
            // No 'enabled' param on M3 FAB
            FloatingActionButton(onClick = { /* no-op */ }) {
                Icon(Icons.Default.Add, null)
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            // Rest toggle
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Rest: ${if (useWorkoutTimer) restWorkouts else restSets}s")
                IconToggleButton(
                    checked = useWorkoutTimer,
                    onCheckedChange = { vm.toggleTimerChoice(it) }
                ) {
                    Icon(
                        imageVector = if (useWorkoutTimer) Icons.Default.Timer else Icons.Default.AccessTime,
                        contentDescription = null
                    )
                }
            }

            // Sets list
            LazyColumn(
                Modifier
                    .weight(1f)
                    .padding(8.dp)
            ) {
                items(sets) { s ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .border(1.dp, Color.Gray, MaterialTheme.shapes.small),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Weight: ${s.weightKg} kg", Modifier.padding(8.dp))
                        Row {
                            IconButton(onClick = { setDialog = true to s }) {
                                Icon(Icons.Default.Edit, null)
                            }
                            IconButton(onClick = { vm.deleteSet(s) }) {
                                Icon(Icons.Default.Delete, null)
                            }
                        }
                    }
                }
            }

            // Bottom buttons
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Button(onClick = { setDialog = true to null }, enabled = selected != null) {
                    Icon(Icons.Default.Add, null); Spacer(Modifier.width(4.dp)); Text("Add Set")
                }
                Button(onClick = { showTimerSettings = true }) {
                    Icon(Icons.Default.Settings, null); Spacer(Modifier.width(4.dp)); Text("Settings")
                }
                Button(onClick = { showCountdown = true }) {
                    Icon(Icons.Default.PlayArrow, null); Spacer(Modifier.width(4.dp)); Text("Start")
                }
            }
        }

        // Workout dialog
        if (workoutDialog.first) {
            WorkoutDialog(
                title = if (workoutDialog.second == null) "Add Workout" else "Edit Workout",
                initial = workoutDialog.second,
                onConfirm = { name ->
                    if (workoutDialog.second == null) vm.addWorkout(name)
                    else vm.editWorkout(vm.selectedWorkout.value!!.copy(name = name))
                    workoutDialog = false to null
                },
                onDismiss = { workoutDialog = false to null }
            )
        }

        // Set dialog
        if (setDialog.first) {
            SetDialog(
                title = if (setDialog.second == null) "Add Set" else "Edit Set",
                initial = setDialog.second?.weightKg,
                onConfirm = { wt ->
                    if (setDialog.second == null) vm.addSet(wt)
                    else vm.editSet(setDialog.second!!.copy(weightKg = wt))
                    setDialog = false to null
                },
                onDismiss = { setDialog = false to null }
            )
        }

        // Timer settings
        if (showTimerSettings) {
            RestTimerDialog(
                defaultSet = restSets,
                defaultWorkout = restWorkouts,
                onSave = { s, w ->
                    vm.updateRestBetweenSets(s)
                    vm.updateRestBetweenWorkouts(w)
                    showTimerSettings = false
                },
                onDismiss = { showTimerSettings = false }
            )
        }

        // Countdown
        if (showCountdown) {
            RestCountdownDialog(
                seconds = if (useWorkoutTimer) restWorkouts else restSets,
                onFinish = { showCountdown = false },
                onCancel = { showCountdown = false }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    MainScreen()
}