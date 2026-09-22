package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitDao {
    @Query("SELECT * FROM habits")
    fun getAllHabits(): Flow<List<Habit>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabit(habit: Habit): Long

    @Delete
    suspend fun deleteHabit(habit: Habit)

    @Query("SELECT * FROM habits WHERE category = :category")
    fun getHabitsByCategory(category: String): Flow<List<Habit>>
}

@Dao
interface HabitLogDao {
    @Query("SELECT * FROM habit_logs WHERE dateString = :dateString")
    fun getLogsForDate(dateString: String): Flow<List<HabitLog>>

    @Query("SELECT * FROM habit_logs WHERE habitId = :habitId")
    fun getLogsForHabit(habitId: Int): Flow<List<HabitLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: HabitLog)

    @Query("SELECT * FROM habit_logs WHERE dateString >= :startDate AND dateString <= :endDate")
    fun getLogsInRange(startDate: String, endDate: String): Flow<List<HabitLog>>

    @Query("DELETE FROM habit_logs WHERE habitId = :habitId")
    suspend fun deleteLogsForHabit(habitId: Int)

    @Query("DELETE FROM habit_logs WHERE habitId = :habitId AND dateString = :dateString")
    suspend fun deleteLogForHabitOnDate(habitId: Int, dateString: String)
}

@Dao
interface WorkoutLogDao {
    @Query("SELECT * FROM workout_logs ORDER BY dateString DESC, id DESC")
    fun getAllWorkouts(): Flow<List<WorkoutLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkout(workout: WorkoutLog)

    @Delete
    suspend fun deleteWorkout(workout: WorkoutLog)

    @Query("SELECT * FROM workout_logs WHERE dateString = :dateString")
    fun getWorkoutsForDate(dateString: String): Flow<List<WorkoutLog>>
}

@Dao
interface SleepLogDao {
    @Query("SELECT * FROM sleep_logs ORDER BY dateString DESC")
    fun getAllSleepLogs(): Flow<List<SleepLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSleepLog(sleepLog: SleepLog)

    @Delete
    suspend fun deleteSleepLog(sleepLog: SleepLog)

    @Query("SELECT * FROM sleep_logs WHERE dateString = :dateString LIMIT 1")
    suspend fun getSleepLogForDate(dateString: String): SleepLog?
}
