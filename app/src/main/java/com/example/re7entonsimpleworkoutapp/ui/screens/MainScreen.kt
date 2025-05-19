package com.example.re7entonsimpleworkoutapp.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
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
import androidx.compose.ui.text.style.TextOverflow
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
    // 1) Observe state
    val workouts by vm.workouts.collectAsState()
    val selected by vm.selectedWorkout.collectAsState()
    val sets by vm.sets.collectAsState()
    val restSets by vm.restBetweenSets.collectAsState()
    val restWorkouts by vm.restBetweenWorkouts.collectAsState()
    val useWorkoutTimer by vm.useWorkoutTimer.collectAsState()

    // 2) Local UI state
    var dropdownExpanded by remember { mutableStateOf(false) }
    var workoutDialog by remember { mutableStateOf<Pair<Boolean, String?>>(false to null) }
    var setDialog by remember { mutableStateOf<Pair<Boolean, WorkoutSet?>>(false to null) }
    var showTimerSettings by remember { mutableStateOf(false) }
    var showCountdown by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    // Dropdown selector
                    Row {
                        Text(
                            text = selected?.name ?: "Select Workout",
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .clickable(enabled = workouts.isNotEmpty()) {
                                    dropdownExpanded = !dropdownExpanded
                                }
                                .padding(8.dp),
                            maxLines = 1, overflow = TextOverflow.Ellipsis
                        )
                        Icon(
                            Icons.Default.ArrowDropDown,
                            contentDescription = "Open workouts",
                            modifier = Modifier
                                .clickable(enabled = workouts.isNotEmpty()) {
                                    dropdownExpanded = !dropdownExpanded
                                }
                                .padding(end = 8.dp)
                        )
                        DropdownMenu(
                            expanded = dropdownExpanded,
                            onDismissRequest = { dropdownExpanded = false }
                        ) {
                            workouts.forEach { w ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            w.name,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    },
                                    onClick = {
                                        vm.selectWorkout(w)
                                        dropdownExpanded = false
                                        Timber.d("Selected workout: ${w.name}")
                                    },
                                    trailingIcon = {
                                        IconButton(onClick = {
                                            workoutDialog = true to w.name
                                        }) {
                                            Icon(Icons.Default.Edit, contentDescription = "Edit")
                                        }
                                        IconButton(onClick = {
                                            vm.deleteWorkout(w)
                                        }) {
                                            Icon(Icons.Default.Delete, contentDescription = "Delete")
                                        }
                                    }
                                )
                            }
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { workoutDialog = true to null }) {
                        Icon(Icons.Default.Add, contentDescription = "Add workout")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            Spacer(Modifier.height(8.dp))

            // Show selected
            selected?.let {
                Text(
                    it.name,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(horizontal = 16.dp),
                    maxLines = 1, overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(8.dp))
            }

            // 3 buttons, equal width
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Add Set
                Button(
                    onClick = { setDialog = true to null },
                    enabled = selected != null,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add set")
                    Spacer(Modifier.width(4.dp))
                    Text(
                        "Add Set",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                // Timer Settings
                Button(
                    onClick = { showTimerSettings = true },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Timer, contentDescription = "Timer settings")
                    Spacer(Modifier.width(4.dp))
                    Text(
                        "Timer Settings",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                // Start
                Button(
                    onClick = { showCountdown = true },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = "Start rest")
                    Spacer(Modifier.width(4.dp))
                    Text(
                        "Start",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Sets list
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
                        Text(
                            "Weight: ${s.weightKg} kg",
                            Modifier.padding(8.dp),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Row {
                            IconButton(onClick = { setDialog = true to s }) {
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

        // Workout dialog
        if (workoutDialog.first) {
            WorkoutDialog(
                title = if (workoutDialog.second == null) "Add Workout" else "Edit Workout",
                initial = workoutDialog.second,
                onConfirm = { name ->
                    if (workoutDialog.second == null) vm.addWorkout(name)
                    else vm.selectedWorkout.value
                        ?.copy(name = name)
                        ?.let { vm.editWorkout(it) }
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
                useWorkoutTimer = useWorkoutTimer,
                onSave = { s, w, choice ->
                    vm.updateRestBetweenSets(s)
                    vm.updateRestBetweenWorkouts(w)
                    vm.toggleTimerChoice(choice)
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