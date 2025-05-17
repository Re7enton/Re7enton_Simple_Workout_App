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
import androidx.compose.material3.DropdownMenu
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
import androidx.compose.ui.text.font.FontWeight
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
fun MainScreen(
    modifier: Modifier = Modifier,
    vm: WorkoutViewModel = hiltViewModel(),
) {
    // 1) Observe state from the ViewModel
    val workouts by vm.workouts.collectAsState()
    val selected by vm.selectedWorkout.collectAsState()
    val sets by vm.sets.collectAsState()
    val restSets by vm.restBetweenSets.collectAsState()
    val restWorkouts by vm.restBetweenWorkouts.collectAsState()
    val useWorkoutTimer by vm.useWorkoutTimer.collectAsState()

    // 2) UI-local state
    var dropdownExpanded by remember { mutableStateOf(false) }
    var workoutDialogState by remember { mutableStateOf<Pair<Boolean, String?>>(false to null) }
    var setDialogState by remember { mutableStateOf<Pair<Boolean, WorkoutSet?>>(false to null) }
    var showTimerSettings by remember { mutableStateOf(false) }
    var showCountdown by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    // Workout selector: text + dropdown menu
                    Row {
                        Text(
                            text = selected?.name ?: "Select Workout",
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                // clickable only if items exist
                                .clickable(enabled = workouts.isNotEmpty()) {
                                    dropdownExpanded = !dropdownExpanded
                                }
                                .padding(8.dp)
                        )
                        DropdownMenu(
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
                                        // Edit button
                                        IconButton(onClick = {
                                            workoutDialogState = true to w.name
                                        }) {
                                            Icon(Icons.Default.Edit, contentDescription = "Edit workout")
                                        }
                                        // Delete button
                                        IconButton(onClick = { vm.deleteWorkout(w) }) {
                                            Icon(Icons.Default.Delete, contentDescription = "Delete workout")
                                        }
                                    }
                                )
                            }
                        }
                    }
                },
                actions = {
                    // Single Add‑Workout button
                    IconButton(onClick = { workoutDialogState = true to null }) {
                        Icon(Icons.Default.Add, contentDescription = "Add workout")
                    }
                }
            )
        },
        // Remove extra FAB entirely
        floatingActionButton = {}
    ) { padding ->
        Column(
            modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            Spacer(Modifier.height(8.dp))

            // 3) Display selected workout name prominently
            selected?.let {
                Text(
                    text = it.name,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(Modifier.height(8.dp))
            }

            // 4) Buttons under workout selector
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { setDialogState = true to null },
                    enabled = selected != null
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add set")
                    Spacer(Modifier.width(4.dp))
                    Text("Add Set")
                }
                Button(onClick = { showTimerSettings = true }) {
                    Icon(Icons.Default.Settings, contentDescription = "Timer settings")
                    Spacer(Modifier.width(4.dp))
                    Text("Settings")
                }
                Button(
                    onClick = { showCountdown = true },
                    enabled = selected != null
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = "Start rest")
                    Spacer(Modifier.width(4.dp))
                    Text("Start")
                }
            }

            Spacer(Modifier.height(16.dp))

            // 5) List of sets with edit/delete
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
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
                            IconButton(onClick = { setDialogState = true to s }) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit set")
                            }
                            IconButton(onClick = { vm.deleteSet(s) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete set")
                            }
                        }
                    }
                }
            }
        }

        // 6) Workout add/edit dialog
        if (workoutDialogState.first) {
            WorkoutDialog(
                title = if (workoutDialogState.second == null) "Add Workout" else "Edit Workout",
                initial = workoutDialogState.second,
                onConfirm = { name ->
                    if (workoutDialogState.second == null) {
                        vm.addWorkout(name)
                    } else {
                        vm.selectedWorkout.value
                            ?.copy(name = name)
                            ?.let { vm.editWorkout(it) }
                    }
                    workoutDialogState = false to null
                },
                onDismiss = { workoutDialogState = false to null }
            )
        }

        // 7) Set add/edit dialog
        if (setDialogState.first) {
            SetDialog(
                title = if (setDialogState.second == null) "Add Set" else "Edit Set",
                initial = setDialogState.second?.weightKg,
                onConfirm = { weight ->
                    if (setDialogState.second == null) {
                        vm.addSet(weight)
                    } else {
                        setDialogState.second!!
                            .copy(weightKg = weight)
                            .let { vm.editSet(it) }
                    }
                    setDialogState = false to null
                },
                onDismiss = { setDialogState = false to null }
            )
        }

        // 8) Rest timer settings dialog
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

        // 9) Rest countdown dialog
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