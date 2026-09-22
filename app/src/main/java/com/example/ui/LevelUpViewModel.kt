package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.Habit
import com.example.data.HabitLog
import com.example.data.LevelUpRepository
import com.example.data.SleepLog
import com.example.data.WorkoutLog
import com.example.network.GeminiApiHelper
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

data class StretchExercise(
    val name: String,
    val durationSeconds: Int,
    val description: String
)

class LevelUpViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: LevelUpRepository
    private val prefs = application.getSharedPreferences("levelup_prefs", android.content.Context.MODE_PRIVATE)

    init {
        val database = AppDatabase.getDatabase(application)
        repository = LevelUpRepository(database)
        
        // Initialize default habits
        viewModelScope.launch {
            repository.prepopulateDefaultsIfEmpty()
            delay(2500) // Show loading splash for 2.5 seconds
            _isAppInitializing.value = false
        }
    }

    private val _isAppInitializing = MutableStateFlow(true)
    val isAppInitializing: StateFlow<Boolean> = _isAppInitializing.asStateFlow()

    private val _isDarkTheme = MutableStateFlow(prefs.getBoolean("dark_theme", true))
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    fun toggleTheme() {
        val newVal = !_isDarkTheme.value
        _isDarkTheme.value = newVal
        prefs.edit().putBoolean("dark_theme", newVal).apply()
    }

    private val _selectedDate = MutableStateFlow(getTodayDateString())
    val selectedDate: StateFlow<String> = _selectedDate.asStateFlow()

    // Habit State
    val allHabits: StateFlow<List<Habit>> = repository.allHabits
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val habitLogsForSelectedDate: StateFlow<List<HabitLog>> = _selectedDate
        .flatMapLatest { date -> repository.getLogsForDate(date) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Workout State
    val allWorkouts: StateFlow<List<WorkoutLog>> = repository.allWorkouts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Sleep State
    val allSleepLogs: StateFlow<List<SleepLog>> = repository.allSleepLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // 7-Day History stats for custom visual charts
    val last7DaysStats: StateFlow<List<DailyStat>> = combine(
        allHabits,
        allSleepLogs,
        _selectedDate,
        habitLogsForSelectedDate
    ) { habitsList, sleepLogs, selectedDateVal, selectedDateLogs ->
        val stats = mutableListOf<DailyStat>()
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        
        // Generate last 7 days
        for (i in 6 downTo 0) {
            val loopCal = Calendar.getInstance()
            loopCal.add(Calendar.DAY_OF_YEAR, -i)
            val dateStr = sdf.format(loopCal.time)
            
            // Get logs for this specific date
            val logs = if (dateStr == selectedDateVal) {
                selectedDateLogs
            } else {
                repository.getLogsForDate(dateStr).first()
            }
            val completedCount = logs.count { it.isCompleted }
            val totalCount = habitsList.size
            
            val pct = if (totalCount > 0) (completedCount.toFloat() / totalCount * 100).toInt() else 0
            
            // Get sleep hours for this date
            val sleepForDate = sleepLogs.find { it.dateString == dateStr }
            val sleepHours = sleepForDate?.durationHours ?: 0.0

            val dayName = SimpleDateFormat("EEE", Locale.getDefault()).format(loopCal.time)
            stats.add(DailyStat(dayName, dateStr, pct, sleepHours))
        }
        stats
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Guided Meditation State
    private val _meditationTimerSeconds = MutableStateFlow(600) // 10 mins default
    val meditationTimerSeconds: StateFlow<Int> = _meditationTimerSeconds.asStateFlow()

    private val _meditationTimerActive = MutableStateFlow(false)
    val meditationTimerActive: StateFlow<Boolean> = _meditationTimerActive.asStateFlow()

    private val _breathingStage = MutableStateFlow("Inhale") // "Inhale", "Hold", "Exhale", "Rest"
    val breathingStage: StateFlow<String> = _breathingStage.asStateFlow()

    private val _breathingScale = MutableStateFlow(1.0f) // Scale for pulsing circle visualizer
    val breathingScale: StateFlow<Float> = _breathingScale.asStateFlow()

    private var meditationJob: Job? = null
    private var breathingJob: Job? = null

    // Evening Stretch State
    val stretchesList = listOf(
        StretchExercise("Cobra Stretch", 45, "Lie face down, lift your chest up with arms extended to stretch and decompress the lower back spine."),
        StretchExercise("Super Ceiling Stretch", 45, "Interlock your fingers, reach towards the sky, and extend fully onto your tiptoes to lengthen the entire skeletal frame."),
        StretchExercise("Bridge Pose", 45, "Lie on your back, bend knees, and push your pelvis high up to stretch hip flexors and decompress spine."),
        StretchExercise("Deep Forward Fold", 45, "Hinge at the hips, keeping legs straight, and let your head and arms hang heavy to decompress vertebrae."),
        StretchExercise("Vertebrae Wall Stretch", 45, "Stand flat against a wall, reach hands high up while maintaining full spine contact to stretch your neck and spine.")
    )

    private val _currentStretchIndex = MutableStateFlow(0)
    val currentStretchIndex: StateFlow<Int> = _currentStretchIndex.asStateFlow()

    private val _stretchTimerSeconds = MutableStateFlow(45)
    val stretchTimerSeconds: StateFlow<Int> = _stretchTimerSeconds.asStateFlow()

    private val _stretchTimerActive = MutableStateFlow(false)
    val stretchTimerActive: StateFlow<Boolean> = _stretchTimerActive.asStateFlow()

    private var stretchJob: Job? = null

    // AI Advice State
    private val _aiQueryLoading = MutableStateFlow(false)
    val aiQueryLoading: StateFlow<Boolean> = _aiQueryLoading.asStateFlow()

    private val _aiResponse = MutableStateFlow<String?>(null)
    val aiResponse: StateFlow<String?> = _aiResponse.asStateFlow()

    // Actions
    fun setSelectedDate(dateString: String) {
        _selectedDate.value = dateString
    }

    fun toggleHabitCompletion(habitId: Int, isCompleted: Boolean) {
        viewModelScope.launch {
            val date = _selectedDate.value
            if (isCompleted) {
                repository.insertHabitLog(HabitLog(habitId = habitId, dateString = date, isCompleted = true))
            } else {
                repository.deleteLogForHabitOnDate(habitId, date)
            }
        }
    }

    fun addCustomHabit(title: String, category: String, description: String) {
        viewModelScope.launch {
            repository.insertHabit(
                Habit(title = title, category = category, description = description, isCustom = true)
            )
        }
    }

    fun deleteHabit(habit: Habit) {
        viewModelScope.launch {
            repository.deleteHabit(habit)
            repository.habitLogDao.deleteLogsForHabit(habit.id)
        }
    }

    fun addWorkout(exerciseName: String, sets: Int, reps: Int, weight: Double, category: String) {
        viewModelScope.launch {
            repository.insertWorkout(
                WorkoutLog(
                    dateString = _selectedDate.value,
                    exerciseName = exerciseName,
                    sets = sets,
                    reps = reps,
                    weight = weight,
                    category = category
                )
            )
        }
    }

    fun deleteWorkout(workout: WorkoutLog) {
        viewModelScope.launch {
            repository.deleteWorkout(workout)
        }
    }

    fun addSleepLog(hours: Double, quality: Int, notes: String) {
        viewModelScope.launch {
            repository.insertSleepLog(
                SleepLog(
                    dateString = _selectedDate.value,
                    durationHours = hours,
                    qualityRating = quality,
                    notes = notes
                )
            )
        }
    }

    fun deleteSleepLog(sleepLog: SleepLog) {
        viewModelScope.launch {
            repository.deleteSleepLog(sleepLog)
        }
    }

    // AI Advice Call
    fun getGrowthAdvice(topic: String) {
        _aiQueryLoading.value = true
        _aiResponse.value = null
        viewModelScope.launch {
            val systemPrompt = """
                You are the Apex Level Up Coach. Your goal is to guide high-agency individuals to human perfection through biological habit hacking, customized daily growth diets, and precise mental/physical schedules.
                Give highly specific, scientifically validated, actionable biological advice.
                Use bullet points and bold headers. Keep it high-density, action-focused, and highly inspiring.
            """.trimIndent()

            val userPrompt = when (topic) {
                "Hair Density Diet" -> "Plan a comprehensive daily growth diet optimized for maximizing hair follicle thickness and boosting hair density. Highlight specific micro and macro nutrition (e.g. DHT blockers, biotin, iron, essential fatty acids)."
                "Facial Symmetry Protocol" -> "Provide a comprehensive protocol for improving facial symmetry, correct tongue posture (mewing), symmetrical chewing, swallowing patterns, and sleeping ergonomics."
                "Height Stretching after 18" -> "Detail a scientific evening stretching schedule and daily growth habits optimized for decompressing the spine and maximizing height potential after the age of 18."
                "Mental Clarity Routine" -> "Structure an optimal daily schedule and diet to optimize mental clarity, neurogenesis, BDNF, and focused cognitive efficiency."
                else -> "Generate customized habit and growth recommendations for the individual regarding: $topic"
            }

            val result = GeminiApiHelper.queryGemini(userPrompt, systemPrompt)
            _aiResponse.value = result
            _aiQueryLoading.value = false
        }
    }

    // Meditation Timer
    fun startMeditationTimer(durationMinutes: Int) {
        _meditationTimerSeconds.value = durationMinutes * 60
        _meditationTimerActive.value = true
        
        // Start Countdown
        meditationJob?.cancel()
        meditationJob = viewModelScope.launch {
            while (_meditationTimerSeconds.value > 0 && _meditationTimerActive.value) {
                delay(1000)
                _meditationTimerSeconds.value -= 1
            }
            if (_meditationTimerSeconds.value == 0) {
                _meditationTimerActive.value = false
                breathingJob?.cancel()
                _breathingStage.value = "Finished"
                _breathingScale.value = 1.0f
            }
        }

        // Start Breathing Cycle (4s inhale, 4s hold, 4s exhale, 4s hold)
        breathingJob?.cancel()
        breathingJob = viewModelScope.launch {
            while (_meditationTimerActive.value) {
                // Inhale
                _breathingStage.value = "Inhale"
                animateBreathingScale(1.0f, 1.8f, 4000)
                if (!_meditationTimerActive.value) break
                // Hold
                _breathingStage.value = "Hold"
                delay(4000)
                if (!_meditationTimerActive.value) break
                // Exhale
                _breathingStage.value = "Exhale"
                animateBreathingScale(1.8f, 1.0f, 4000)
                if (!_meditationTimerActive.value) break
                // Hold
                _breathingStage.value = "Rest"
                delay(4000)
            }
        }
    }

    private suspend fun animateBreathingScale(start: Float, end: Float, durationMs: Long) {
        val steps = 40
        val delayMs = durationMs / steps
        for (i in 0..steps) {
            if (!_meditationTimerActive.value) break
            val fraction = i.toFloat() / steps
            _breathingScale.value = start + fraction * (end - start)
            delay(delayMs)
        }
    }

    fun pauseMeditationTimer() {
        _meditationTimerActive.value = false
        meditationJob?.cancel()
        breathingJob?.cancel()
        _breathingStage.value = "Paused"
        _breathingScale.value = 1.0f
    }

    fun resetMeditationTimer() {
        pauseMeditationTimer()
        _meditationTimerSeconds.value = 600
        _breathingStage.value = "Ready"
    }

    // Stretching Timer
    fun startStretchTimer() {
        _stretchTimerActive.value = true
        stretchJob?.cancel()
        stretchJob = viewModelScope.launch {
            while (_stretchTimerSeconds.value > 0 && _stretchTimerActive.value) {
                delay(1000)
                _stretchTimerSeconds.value -= 1
            }
            if (_stretchTimerSeconds.value == 0) {
                // Move to next stretch if available
                if (_currentStretchIndex.value < stretchesList.size - 1) {
                    _currentStretchIndex.value += 1
                    _stretchTimerSeconds.value = stretchesList[_currentStretchIndex.value].durationSeconds
                    delay(1000)
                    startStretchTimer()
                } else {
                    _stretchTimerActive.value = false
                    _currentStretchIndex.value = 0
                    _stretchTimerSeconds.value = stretchesList[0].durationSeconds
                }
            }
        }
    }

    fun pauseStretchTimer() {
        _stretchTimerActive.value = false
        stretchJob?.cancel()
    }

    fun resetStretchTimer() {
        pauseStretchTimer()
        _currentStretchIndex.value = 0
        _stretchTimerSeconds.value = stretchesList[0].durationSeconds
    }

    fun skipStretch() {
        pauseStretchTimer()
        if (_currentStretchIndex.value < stretchesList.size - 1) {
            _currentStretchIndex.value += 1
        } else {
            _currentStretchIndex.value = 0
        }
        _stretchTimerSeconds.value = stretchesList[_currentStretchIndex.value].durationSeconds
    }

    private fun getTodayDateString(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Calendar.getInstance().time)
    }

    override fun onCleared() {
        super.onCleared()
        meditationJob?.cancel()
        breathingJob?.cancel()
        stretchJob?.cancel()
    }
}

data class DailyStat(
    val dayName: String,
    val dateString: String,
    val completionPercentage: Int,
    val sleepHours: Double
)
