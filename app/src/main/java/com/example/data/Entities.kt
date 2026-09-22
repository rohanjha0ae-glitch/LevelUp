package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habits")
data class Habit(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val category: String, // "Workout", "Sleep", "Diet", "Meditation", "Evening Stretch", "General"
    val description: String,
    val isCustom: Boolean = false
)

@Entity(tableName = "habit_logs")
data class HabitLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val habitId: Int,
    val dateString: String, // Format: yyyy-MM-dd
    val isCompleted: Boolean
)

@Entity(tableName = "workout_logs")
data class WorkoutLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val dateString: String, // Format: yyyy-MM-dd
    val exerciseName: String,
    val sets: Int,
    val reps: Int,
    val weight: Double,
    val category: String = "Strength"
)

@Entity(tableName = "sleep_logs")
data class SleepLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val dateString: String, // Format: yyyy-MM-dd
    val durationHours: Double,
    val qualityRating: Int, // 1 - 10 scale
    val notes: String = ""
)
