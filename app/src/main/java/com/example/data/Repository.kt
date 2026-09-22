package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class LevelUpRepository(private val db: AppDatabase) {
    val habitDao = db.habitDao()
    val habitLogDao = db.habitLogDao()
    val workoutLogDao = db.workoutLogDao()
    val sleepLogDao = db.sleepLogDao()

    // Habit operations
    val allHabits: Flow<List<Habit>> = habitDao.getAllHabits()
    fun getHabitsByCategory(category: String): Flow<List<Habit>> = habitDao.getHabitsByCategory(category)
    suspend fun insertHabit(habit: Habit): Long = habitDao.insertHabit(habit)
    suspend fun deleteHabit(habit: Habit) = habitDao.deleteHabit(habit)

    // Habit Log operations
    fun getLogsForDate(dateString: String): Flow<List<HabitLog>> = habitLogDao.getLogsForDate(dateString)
    fun getLogsForHabit(habitId: Int): Flow<List<HabitLog>> = habitLogDao.getLogsForHabit(habitId)
    fun getLogsInRange(startDate: String, endDate: String): Flow<List<HabitLog>> = habitLogDao.getLogsInRange(startDate, endDate)
    suspend fun insertHabitLog(log: HabitLog) = habitLogDao.insertLog(log)
    suspend fun deleteLogForHabitOnDate(habitId: Int, dateString: String) = habitLogDao.deleteLogForHabitOnDate(habitId, dateString)

    // Workout operations
    val allWorkouts: Flow<List<WorkoutLog>> = workoutLogDao.getAllWorkouts()
    suspend fun insertWorkout(workout: WorkoutLog) = workoutLogDao.insertWorkout(workout)
    suspend fun deleteWorkout(workout: WorkoutLog) = workoutLogDao.deleteWorkout(workout)
    fun getWorkoutsForDate(dateString: String): Flow<List<WorkoutLog>> = workoutLogDao.getWorkoutsForDate(dateString)

    // Sleep operations
    val allSleepLogs: Flow<List<SleepLog>> = sleepLogDao.getAllSleepLogs()
    suspend fun insertSleepLog(sleepLog: SleepLog) = sleepLogDao.insertSleepLog(sleepLog)
    suspend fun deleteSleepLog(sleepLog: SleepLog) = sleepLogDao.deleteSleepLog(sleepLog)
    suspend fun getSleepLogForDate(dateString: String): SleepLog? = sleepLogDao.getSleepLogForDate(dateString)

    // Prepopulate default habits based on user requirements
    suspend fun prepopulateDefaultsIfEmpty() {
        val current = allHabits.first()
        if (current.isEmpty()) {
            val defaults = listOf(
                Habit(
                    title = "Evening Height-Growth Stretches",
                    category = "Evening Stretch",
                    description = "Decompress your spine with cobra stretch, bridge, child's pose, and hanging to optimize growth.",
                    isCustom = false
                ),
                Habit(
                    title = "Deep Mental Clarity Meditation",
                    category = "Meditation",
                    description = "10 minutes of guided diaphragmatic breathing to optimize brain-derived neurotrophic factor (BDNF).",
                    isCustom = false
                ),
                Habit(
                    title = "Hair Density Nutrition Intake",
                    category = "Diet",
                    description = "Fuel your body with zinc, biotin, pumpkin seed oil, and protein to maximize hair follicle thickness.",
                    isCustom = false
                ),
                Habit(
                    title = "Symmetry & Facial Exercises",
                    category = "General",
                    description = "Perform mewing (correct tongue posture), symmetrical chewing, and jaw posture correction.",
                    isCustom = false
                ),
                Habit(
                    title = "Growth Hormone Boosting Workout",
                    category = "Workout",
                    description = "Execute a high-intensity workout session to trigger growth hormone release.",
                    isCustom = false
                ),
                Habit(
                    title = "8-Hour Anabolic Sleep",
                    category = "Sleep",
                    description = "Maintain full-dark room conditions, cool temperature, and 8 hours of uninterrupted rest.",
                    isCustom = false
                )
            )
            for (habit in defaults) {
                habitDao.insertHabit(habit)
            }
        }
    }
}
