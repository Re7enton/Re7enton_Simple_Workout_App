package com.example.re7entonsimpleworkoutapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

// Each instance is one workout category/title
@Entity(tableName = "workouts")
data class Workout(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,  // unique ID
    val name: String                                     // e.g. "Squat"
)