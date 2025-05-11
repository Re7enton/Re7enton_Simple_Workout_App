package com.example.re7entonsimpleworkoutapp.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "sets",
    foreignKeys = [
        ForeignKey(
            entity = Workout::class,
            parentColumns = ["id"],
            childColumns = ["workoutId"],
            onDelete = ForeignKey.CASCADE  // delete sets if workout is deleted
        )
    ],
    indices = [Index("workoutId")]
)
data class WorkoutSet(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,  // unique ID
    val workoutId: Long,                                // FK to Workout.id
    val weightKg: Float,                                // weight used
    val timestamp: Long = System.currentTimeMillis()    // when the set was recorded
)