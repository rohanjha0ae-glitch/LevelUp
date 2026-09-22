package com.example.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.Habit
import com.example.data.HabitLog
import com.example.data.SleepLog
import com.example.data.WorkoutLog
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LevelUpMainScreen(viewModel: LevelUpViewModel, authViewModel: AuthViewModel? = null) {
    val colors = LocalLevelUpColors.current
    val Black = colors.background
    val DarkGrey = colors.surface
    val MediumGrey = colors.surfaceVariant
    val PureWhite = colors.textPrimary
    val Slate500 = colors.textSecondary
    val LightGrey = colors.textSecondary
    val OptimalGreen = colors.accentGreen
    val SleepBlue = colors.accentBlue
    val WorkoutRed = colors.accentRed

    var selectedTab by remember { mutableStateOf(0) }
    
    val selectedDate by viewModel.selectedDate.collectAsStateWithLifecycle()
    val habits by viewModel.allHabits.collectAsStateWithLifecycle()
    val logs by viewModel.habitLogsForSelectedDate.collectAsStateWithLifecycle()
    
    val dateSdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val displaySdf = SimpleDateFormat("EEEE, d MMMM yyyy", Locale.getDefault())
    val parsedDate = remember(selectedDate) {
        try { dateSdf.parse(selectedDate) ?: Date() } catch (e: Exception) { Date() }
    }
    val dateDisplayString = remember(parsedDate) { displaySdf.format(parsedDate) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Black)
                    .statusBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 600.dp)
                        .align(Alignment.CenterHorizontally)
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "HUMAN OPTIMIZATION",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Slate500,
                            letterSpacing = 1.5.sp
                        )
                        Text(
                            text = "Roger",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = PureWhite,
                            letterSpacing = (-0.5).sp
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        IconButton(
                            onClick = { viewModel.setSelectedDate(dateSdf.format(Date())) },
                            modifier = Modifier
                                .testTag("reset_date_button")
                                .size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Today,
                                contentDescription = "Today",
                                tint = OptimalGreen,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        val isDarkTheme by viewModel.isDarkTheme.collectAsStateWithLifecycle()
                        IconButton(
                            onClick = { viewModel.toggleTheme() },
                            modifier = Modifier
                                .testTag("toggle_theme_button")
                                .size(36.dp)
                        ) {
                            Icon(
                                imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                                contentDescription = "Toggle Theme",
                                tint = OptimalGreen,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(MediumGrey, shape = CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "R",
                                color = PureWhite,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Black)
                    .navigationBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 600.dp)
                        .align(Alignment.CenterHorizontally)
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val items = listOf(
                        NavigationItem("Dashboard", Icons.Default.Dashboard, Icons.Outlined.Dashboard, 0),
                        NavigationItem("Log", Icons.Default.FitnessCenter, Icons.Outlined.FitnessCenter, 1),
                        NavigationItem("Stretch", Icons.Default.AccessibilityNew, Icons.Outlined.AccessibilityNew, 2),
                        NavigationItem("Meditate", Icons.Default.Spa, Icons.Outlined.Spa, 3),
                        NavigationItem("AI Coach", Icons.Default.Psychology, Icons.Outlined.Psychology, 4),
                        NavigationItem("Profile", Icons.Default.Person, Icons.Outlined.Person, 5)
                    )
                    
                    items.forEach { item ->
                        val isSelected = selectedTab == item.index
                        Column(
                            modifier = Modifier
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null,
                                    onClick = { selectedTab = item.index }
                                )
                                .weight(1f)
                                .testTag("nav_tab_${item.index}"),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                contentDescription = item.label,
                                tint = if (isSelected) OptimalGreen else Slate500,
                                modifier = Modifier.size(22.dp)
                            )
                            Text(
                                text = item.label,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
                                color = if (isSelected) PureWhite else Slate500
                            )
                        }
                    }
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 600.dp)
            ) {
                when (selectedTab) {
                    0 -> DashboardTab(viewModel, habits, logs, selectedDate, dateDisplayString)
                    1 -> LogRoutinesTab(viewModel, selectedDate)
                    2 -> EveningStretchingTab(viewModel)
                    3 -> GuidedMeditationTab(viewModel)
                    4 -> AiAdvisorTab(viewModel)
                    5 -> if (authViewModel != null) ProfileTab(authViewModel)
                }
            }
        }
    }
}

data class NavigationItem(
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val index: Int
)

@Composable
fun ConcentricStabilityRing(
    habitPct: Float,
    sleepPct: Float,
    workoutPct: Float,
    stabilityIndex: Float,
    modifier: Modifier = Modifier
) {
    val colors = LocalLevelUpColors.current
    val Black = colors.background
    val DarkGrey = colors.surface
    val MediumGrey = colors.surfaceVariant
    val PureWhite = colors.textPrimary
    val Slate500 = colors.textSecondary
    val LightGrey = colors.textSecondary
    val OptimalGreen = colors.accentGreen
    val SleepBlue = colors.accentBlue
    val WorkoutRed = colors.accentRed

    val displayHabit = if (habitPct > 0f) habitPct else 0.72f
    val displaySleep = if (sleepPct > 0f) sleepPct else 0.65f
    val displayWorkout = if (workoutPct > 0f) workoutPct else 0.5f

    Box(
        modifier = modifier
            .size(240.dp)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val strokeWidth = 12.dp.toPx()
            
            // Draw backgrounds for tracks
            drawCircle(
                color = colors.trackBg,
                radius = 104.dp.toPx() - strokeWidth / 2,
                center = center,
                style = Stroke(width = strokeWidth)
            )
            drawCircle(
                color = colors.trackBg,
                radius = 86.dp.toPx() - strokeWidth / 2,
                center = center,
                style = Stroke(width = strokeWidth)
            )
            drawCircle(
                color = colors.trackBg,
                radius = 68.dp.toPx() - strokeWidth / 2,
                center = center,
                style = Stroke(width = strokeWidth)
            )

            // Draw progress arcs
            // Outer (Green)
            drawArc(
                color = OptimalGreen,
                startAngle = -90f,
                sweepAngle = 360f * displayHabit,
                useCenter = false,
                topLeft = Offset(center.x - 104.dp.toPx() + strokeWidth / 2, center.y - 104.dp.toPx() + strokeWidth / 2),
                size = Size((208.dp.toPx() - strokeWidth), (208.dp.toPx() - strokeWidth)),
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            // Middle (Blue)
            drawArc(
                color = SleepBlue,
                startAngle = -90f,
                sweepAngle = 360f * displaySleep,
                useCenter = false,
                topLeft = Offset(center.x - 86.dp.toPx() + strokeWidth / 2, center.y - 86.dp.toPx() + strokeWidth / 2),
                size = Size((172.dp.toPx() - strokeWidth), (172.dp.toPx() - strokeWidth)),
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            // Inner (Red)
            drawArc(
                color = WorkoutRed,
                startAngle = -90f,
                sweepAngle = 360f * displayWorkout,
                useCenter = false,
                topLeft = Offset(center.x - 68.dp.toPx() + strokeWidth / 2, center.y - 68.dp.toPx() + strokeWidth / 2),
                size = Size((136.dp.toPx() - strokeWidth), (136.dp.toPx() - strokeWidth)),
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = String.format(Locale.getDefault(), "%.1f", stabilityIndex),
                fontSize = 44.sp,
                fontWeight = FontWeight.Black,
                color = PureWhite,
                letterSpacing = (-1.5).sp
            )
            Text(
                text = "STABILITY INDEX",
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = Slate500,
                letterSpacing = 1.5.sp
            )
        }
    }
}

@Composable
fun MetricGrid(
    habitPct: Float,
    isMeditated: Boolean,
    isStretched: Boolean,
    sleepLog: SleepLog?
) {
    val colors = LocalLevelUpColors.current
    val Black = colors.background
    val DarkGrey = colors.surface
    val MediumGrey = colors.surfaceVariant
    val PureWhite = colors.textPrimary
    val Slate500 = colors.textSecondary
    val LightGrey = colors.textSecondary
    val OptimalGreen = colors.accentGreen
    val SleepBlue = colors.accentBlue
    val WorkoutRed = colors.accentRed
    val TranslucentCardBg = colors.surface
    val TranslucentCardBorder = Color.Transparent

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Card 1: Growth (Green)
            Surface(
                modifier = Modifier.weight(1f),
                color = TranslucentCardBg,
                shape = RoundedCornerShape(28.dp),
                border = BorderStroke(1.dp, TranslucentCardBorder)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Spa,
                            contentDescription = "Growth",
                            tint = OptimalGreen,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "GROWTH",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Slate500,
                            letterSpacing = 1.sp
                        )
                    }
                    Column {
                        Text(
                            text = "Hair Density",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = PureWhite
                        )
                        Text(
                            text = if (habitPct > 0f) "+4.2% (Active)" else "+4.2% This Week",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = OptimalGreen
                        )
                    }
                }
            }

            // Card 2: Mind (Blue)
            Surface(
                modifier = Modifier.weight(1f),
                color = TranslucentCardBg,
                shape = RoundedCornerShape(28.dp),
                border = BorderStroke(1.dp, TranslucentCardBorder)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Psychology,
                            contentDescription = "Mind",
                            tint = SleepBlue,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "MIND",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Slate500,
                            letterSpacing = 1.sp
                        )
                    }
                    Column {
                        Text(
                            text = "Daily Clarity",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = PureWhite
                        )
                        Text(
                            text = if (isMeditated) "Completed today" else "15m Session Ready",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = SleepBlue
                        )
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Card 3: Height (Red with left border highlight)
            Surface(
                modifier = Modifier.weight(1f),
                color = TranslucentCardBg,
                shape = RoundedCornerShape(28.dp),
                border = BorderStroke(1.dp, TranslucentCardBorder)
            ) {
                Box {
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .width(4.dp)
                            .height(64.dp)
                            .background(WorkoutRed, shape = RoundedCornerShape(topStart = 28.dp, bottomStart = 28.dp))
                    )
                    
                    Column(
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Straighten,
                                contentDescription = "Height",
                                tint = WorkoutRed,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "HEIGHT",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Slate500,
                                letterSpacing = 1.sp
                            )
                        }
                        Column {
                            Text(
                                text = "Post-18 Dev",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = PureWhite
                            )
                            Text(
                                text = if (isStretched) "Completed today" else "Evening Stretch Ready",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = WorkoutRed,
                                textDecoration = if (isStretched) null else androidx.compose.ui.text.style.TextDecoration.Underline
                            )
                        }
                    }
                }
            }

            // Card 4: Visage (Highlight card!)
            Surface(
                modifier = Modifier.weight(1f),
                color = if (colors.isDark) PureWhite else Black,
                shape = RoundedCornerShape(28.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Face,
                            contentDescription = "Visage",
                            tint = if (colors.isDark) Black else PureWhite,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "VISAGE",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (colors.isDark) Color(0xFF94A3B8) else Slate500,
                            letterSpacing = 1.sp
                        )
                    }
                    Column {
                        Text(
                            text = "Symmetry",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (colors.isDark) Black else PureWhite
                        )
                        Text(
                            text = if (sleepLog != null) "${sleepLog.durationHours}h REM Sleep" else "Optimum Correction",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (colors.isDark) Color(0xFF475569) else Slate500
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SleepQualityBanner(sleepLog: SleepLog?) {
    val colors = LocalLevelUpColors.current
    val Black = colors.background
    val DarkGrey = colors.surface
    val MediumGrey = colors.surfaceVariant
    val PureWhite = colors.textPrimary
    val Slate500 = colors.textSecondary
    val LightGrey = colors.textSecondary
    val OptimalGreen = colors.accentGreen
    val SleepBlue = colors.accentBlue
    val WorkoutRed = colors.accentRed
    val TranslucentCardBg = colors.surface
    val TranslucentCardBorder = Color.Transparent

    val displayDuration = if (sleepLog != null) "${sleepLog.durationHours}h REST" else "Deep REM: 2h 45m"
    val sleepPercentage = if (sleepLog != null) "${((sleepLog.durationHours / 8.0) * 100).toInt().coerceIn(0, 100)}%" else "94%"
    
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = TranslucentCardBg,
        shape = RoundedCornerShape(28.dp),
        border = BorderStroke(1.dp, TranslucentCardBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color(0x336366F1), shape = RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ModeNight,
                        contentDescription = "Sleep Quality",
                        tint = Color(0xFF818CF8),
                        modifier = Modifier.size(20.dp)
                    )
                }
                Column {
                    Text(
                        text = "Sleep Quality",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = PureWhite
                    )
                    Text(
                        text = displayDuration,
                        fontSize = 10.sp,
                        color = Slate500
                    )
                }
            }
            Text(
                text = sleepPercentage,
                fontSize = 14.sp,
                fontWeight = FontWeight.Black,
                color = PureWhite
            )
        }
    }
}

// ==========================================
// TAB 0: DASHBOARD
// ==========================================
@Composable
fun DashboardTab(
    viewModel: LevelUpViewModel,
    habits: List<Habit>,
    logs: List<HabitLog>,
    selectedDate: String,
    dateDisplayString: String
) {
    val colors = LocalLevelUpColors.current
    val Black = colors.background
    val DarkGrey = colors.surface
    val MediumGrey = colors.surfaceVariant
    val PureWhite = colors.textPrimary
    val Slate500 = colors.textSecondary
    val LightGrey = colors.textSecondary
    val OptimalGreen = colors.accentGreen
    val SleepBlue = colors.accentBlue
    val WorkoutRed = colors.accentRed
    val TranslucentCardBg = colors.surface
    val TranslucentCardBorder = Color.Transparent

    var showAddCustomDialog by remember { mutableStateOf(false) }
    val last7DaysStats by viewModel.last7DaysStats.collectAsStateWithLifecycle()
    val allSleepLogs by viewModel.allSleepLogs.collectAsStateWithLifecycle()
    val allWorkouts by viewModel.allWorkouts.collectAsStateWithLifecycle()

    // Rest, Frame, Vitality local states for real interaction
    var circadianSync by remember { mutableStateOf(true) }
    var postureTimerActive by remember { mutableStateOf(false) }
    var postureTimerSeconds by remember { mutableStateOf(1200) } // 20 mins default

    LaunchedEffect(postureTimerActive) {
        if (postureTimerActive) {
            while (postureTimerSeconds > 0 && postureTimerActive) {
                delay(1000)
                postureTimerSeconds--
            }
            if (postureTimerSeconds == 0) {
                postureTimerActive = false
            }
        }
    }

    var follicleChecked by remember { mutableStateOf(true) }
    var keratinChecked by remember { mutableStateOf(false) }
    var capillaryChecked by remember { mutableStateOf(false) }

    // Metric state calculations
    val todayHabitsCount = habits.size
    val todayCompletedCount = logs.count { it.isCompleted }
    val habitPct = if (todayHabitsCount > 0) todayCompletedCount.toFloat() / todayHabitsCount else 0.0f

    val sleepLog = allSleepLogs.find { it.dateString == selectedDate }
    val sleepPct = if (sleepLog != null) (sleepLog.durationHours / 8.0).toFloat().coerceIn(0f, 1f) else 0.0f

    val workoutLogs = allWorkouts.filter { it.dateString == selectedDate }
    val workoutPct = if (workoutLogs.isNotEmpty()) 1.0f else 0.0f

    val hasAnyActivity = logs.any { it.isCompleted } || sleepLog != null || workoutLogs.isNotEmpty()
    val stabilityIndex = if (hasAnyActivity) {
        (habitPct * 0.4f + sleepPct * 0.4f + workoutPct * 0.2f) * 100f
    } else {
        82.4f // Premium default theme baseline
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Horizontal calendar picker
        item {
            HorizontalCalendarSelector(viewModel, selectedDate)
        }

        // 1. SYSTEM STABILITY SCORE (Thin Progress Line & Status)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column {
                        Text(
                            text = "SYSTEM STABILITY SCORE",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Slate500,
                            letterSpacing = 1.5.sp
                        )
                        Text(
                            text = "Aeterna Index",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = PureWhite
                        )
                    }
                    Text(
                        text = String.format(Locale.getDefault(), "%.1f%%", stabilityIndex),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = OptimalGreen
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                // Thin progress line
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp)
                        .background(MediumGrey, shape = RoundedCornerShape(1.5.dp))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(stabilityIndex / 100f)
                            .fillMaxHeight()
                            .background(OptimalGreen, shape = RoundedCornerShape(1.5.dp))
                    )
                }
            }
        }

        // 2. REST SECTION
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text(
                    text = "CIRCADIAN & BRAIN STATE",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Slate500,
                    letterSpacing = 1.5.sp,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                Text(
                    text = "Rest",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = PureWhite,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = TranslucentCardBg),
                    border = BorderStroke(0.dp, Color.Transparent)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Circadian sync toggle
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Circadian Sync Protocol",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = PureWhite
                                )
                                Text(
                                    text = if (circadianSync) "Phase locked with daylight" else "Desynced from daylight",
                                    fontSize = 12.sp,
                                    color = Slate500
                                )
                            }
                            Switch(
                                checked = circadianSync,
                                onCheckedChange = { circadianSync = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = PureWhite,
                                    checkedTrackColor = OptimalGreen,
                                    uncheckedThumbColor = Slate500,
                                    uncheckedTrackColor = MediumGrey
                                )
                            )
                        }
                        
                        HorizontalDivider(color = Color(0x1F636366), thickness = 0.5.dp)
                        
                        // Sleep quality status
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Sleep Quality",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = PureWhite
                                )
                                Text(
                                    text = if (sleepLog != null) "Sleep: ${(sleepPct * 100).toInt()}% • Optimal" else "Sleep: 88% • Optimal",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = OptimalGreen
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            val currentSleepPct = if (sleepLog != null) sleepPct else 0.88f
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(3.dp)
                                    .background(MediumGrey, shape = RoundedCornerShape(1.5.dp))
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(currentSleepPct)
                                        .fillMaxHeight()
                                        .background(OptimalGreen, shape = RoundedCornerShape(1.5.dp))
                                )
                            }
                        }
                    }
                }
            }
        }

        // 3. FRAME SECTION
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text(
                    text = "SKELETAL INTEGRITY & ALIGNMENT",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Slate500,
                    letterSpacing = 1.5.sp,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                Text(
                    text = "Frame",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = PureWhite,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = TranslucentCardBg),
                    border = BorderStroke(0.dp, Color.Transparent)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Posture alignment timer
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Posture Spine Alignment",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = PureWhite
                                )
                                Text(
                                    text = "Target: 20 minutes daily",
                                    fontSize = 12.sp,
                                    color = Slate500
                                )
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(
                                    text = String.format(Locale.getDefault(), "%02d:%02d", postureTimerSeconds / 60, postureTimerSeconds % 60),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PureWhite,
                                    modifier = Modifier.testTag("posture_countdown")
                                )
                                Text(
                                    text = if (postureTimerActive) "PAUSE" else "START",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = OptimalGreen,
                                    modifier = Modifier
                                        .clickable { postureTimerActive = !postureTimerActive }
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                        .testTag("posture_timer_button")
                                )
                            }
                        }
                        
                        HorizontalDivider(color = Color(0x1F636366), thickness = 0.5.dp)
                        
                        // Symmetry status
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Symmetry Status",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = PureWhite
                                )
                                Text(
                                    text = "Symmetry: 96% • Restored",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = OptimalGreen
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(3.dp)
                                    .background(MediumGrey, shape = RoundedCornerShape(1.5.dp))
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(0.96f)
                                        .fillMaxHeight()
                                        .background(OptimalGreen, shape = RoundedCornerShape(1.5.dp))
                                )
                            }
                        }
                    }
                }
            }
        }

        // 4. VITALITY SECTION
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text(
                    text = "BIOMARKERS & HAIR NUTRITION",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Slate500,
                    letterSpacing = 1.5.sp,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                Text(
                    text = "Vitality",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = PureWhite,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = TranslucentCardBg),
                    border = BorderStroke(0.dp, Color.Transparent)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(
                            text = "Daily Biomarker Tracking",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = PureWhite
                        )
                        
                        val itemsList = listOf(
                            Triple("Follicle Nutrition (DHT Blockers)", follicleChecked, { follicleChecked = !follicleChecked }),
                            Triple("Keratin Synthesis (Biotin 10k)", keratinChecked, { keratinChecked = !keratinChecked }),
                            Triple("Capillary Flow (Scalp Massage)", capillaryChecked, { capillaryChecked = !capillaryChecked })
                        )
                        
                        itemsList.forEach { (label, isChecked, toggle) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { toggle() }
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 14.sp,
                                    color = if (isChecked) PureWhite else Slate500,
                                    fontWeight = if (isChecked) FontWeight.Medium else FontWeight.Normal
                                )
                                Box(
                                    modifier = Modifier
                                        .size(18.dp)
                                        .background(
                                            color = if (isChecked) OptimalGreen else MediumGrey,
                                            shape = CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isChecked) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Checked",
                                            tint = Black,
                                            modifier = Modifier.size(12.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Active Date Indicator
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "HABIT PROTOCOL",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate500,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Active Routines",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = PureWhite
                    )
                }
                
                Button(
                    onClick = { showAddCustomDialog = true },
                    modifier = Modifier.testTag("add_custom_habit_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = OptimalGreen),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add", tint = Black, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Habit", color = Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Habits List
        if (habits.isEmpty()) {
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = TranslucentCardBg,
                    shape = RoundedCornerShape(28.dp),
                    border = BorderStroke(1.dp, TranslucentCardBorder)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.HourglassEmpty,
                            contentDescription = "Empty",
                            modifier = Modifier.size(48.dp),
                            tint = WorkoutRed
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No Habits Configured", fontWeight = FontWeight.Bold, color = PureWhite)
                        Text("Default habits are loading...", fontSize = 12.sp, color = LightGrey)
                    }
                }
            }
        } else {
            items(habits, key = { it.id }) { habit ->
                val isCompleted = logs.any { it.habitId == habit.id && it.isCompleted }
                HabitItemCard(
                    habit = habit,
                    isCompleted = isCompleted,
                    onCheckedChange = { checked ->
                        viewModel.toggleHabitCompletion(habit.id, checked)
                    },
                    onDelete = {
                        viewModel.deleteHabit(habit)
                    }
                )
            }
        }

        // Section: Analytics Charts
        item {
            Text(
                text = "BIOMETRIC ANALYTICS",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 12.sp,
                letterSpacing = 1.sp,
                color = SleepBlue,
                modifier = Modifier.padding(top = 12.dp)
            )
        }

        // Chart 1: Habit Completion % Canvas
        item {
            AnalyticsCard(
                title = "7-Day Habit Adherence Rate (%)",
                description = "Percentage of optimal growth habits checked off",
                accentColor = OptimalGreen
            ) {
                HabitCompletionChart(stats = last7DaysStats)
            }
        }

        // Chart 2: Sleep Duration Canvas
        item {
            AnalyticsCard(
                title = "7-Day Sleep rest duration",
                description = "Daily restorative sleep tracking in hours",
                accentColor = SleepBlue
            ) {
                SleepDurationChart(stats = last7DaysStats)
            }
        }
        
        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    if (showAddCustomDialog) {
        AddCustomHabitDialog(
            onDismiss = { showAddCustomDialog = false },
            onSave = { title, cat, desc ->
                viewModel.addCustomHabit(title, cat, desc)
                showAddCustomDialog = false
            }
        )
    }
}

@Composable
fun HorizontalCalendarSelector(viewModel: LevelUpViewModel, selectedDate: String) {
    val colors = LocalLevelUpColors.current
    val Black = colors.background
    val DarkGrey = colors.surface
    val MediumGrey = colors.surfaceVariant
    val PureWhite = colors.textPrimary
    val Slate500 = colors.textSecondary
    val LightGrey = colors.textSecondary
    val OptimalGreen = colors.accentGreen
    val SleepBlue = colors.accentBlue
    val WorkoutRed = colors.accentRed

    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val dayFormat = SimpleDateFormat("EEE", Locale.getDefault())
    val numFormat = SimpleDateFormat("d", Locale.getDefault())
    
    val days = remember {
        val list = mutableListOf<CalendarDate>()
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -5) // Show last 5 days
        for (i in 0..6) {
            val dateStr = sdf.format(cal.time)
            list.add(
                CalendarDate(
                    dayName = dayFormat.format(cal.time),
                    dayNumber = numFormat.format(cal.time),
                    dateString = dateStr
                )
            )
            cal.add(Calendar.DAY_OF_YEAR, 1)
        }
        list
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        days.forEach { date ->
            val isSelected = date.dateString == selectedDate
            Card(
                modifier = Modifier
                    .width(48.dp)
                    .clickable { viewModel.setSelectedDate(date.dateString) }
                    .testTag("date_select_${date.dateString}"),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) OptimalGreen else DarkGrey
                ),
                border = BorderStroke(0.dp, Color.Transparent)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = date.dayName.uppercase(),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) Black else Slate500
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = date.dayNumber,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (isSelected) Black else PureWhite
                    )
                }
            }
        }
    }
}

data class CalendarDate(
    val dayName: String,
    val dayNumber: String,
    val dateString: String
)

@Composable
fun HabitItemCard(
    habit: Habit,
    isCompleted: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    onDelete: () -> Unit
) {
    val colors = LocalLevelUpColors.current
    val Black = colors.background
    val DarkGrey = colors.surface
    val MediumGrey = colors.surfaceVariant
    val PureWhite = colors.textPrimary
    val Slate500 = colors.textSecondary
    val LightGrey = colors.textSecondary
    val OptimalGreen = colors.accentGreen
    val SleepBlue = colors.accentBlue
    val WorkoutRed = colors.accentRed
    val TranslucentCardBg = colors.surface

    val categoryColor = when (habit.category) {
        "Workout" -> WorkoutRed
        "Sleep" -> SleepBlue
        "Diet" -> OptimalGreen
        "Meditation" -> SleepBlue
        "Evening Stretch" -> WorkoutRed
        else -> PureWhite
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("habit_card_${habit.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCompleted) {
                OptimalGreen.copy(alpha = 0.08f)
            } else {
                TranslucentCardBg
            }
        ),
        border = BorderStroke(0.dp, Color.Transparent)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = isCompleted,
                onCheckedChange = onCheckedChange,
                modifier = Modifier
                    .size(24.dp)
                    .testTag("habit_check_${habit.id}"),
                colors = CheckboxDefaults.colors(
                    checkedColor = OptimalGreen,
                    uncheckedColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    checkmarkColor = Black
                )
            )
            
            Spacer(modifier = Modifier.width(14.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = habit.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = if (isCompleted) PureWhite.copy(alpha = 0.7f) else PureWhite,
                        modifier = Modifier
                            .weight(1f, fill = false)
                            .testTag("habit_title_${habit.id}")
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        color = categoryColor.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = habit.category.uppercase(),
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = categoryColor,
                            maxLines = 1,
                            softWrap = false,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                if (habit.description.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = habit.description,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        lineHeight = 16.sp
                    )
                }
            }
            
            if (habit.isCustom) {
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier
                        .size(32.dp)
                        .testTag("habit_delete_${habit.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Delete Habit",
                        tint = WorkoutRed.copy(alpha = 0.8f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun AnalyticsCard(
    title: String,
    description: String,
    accentColor: Color,
    content: @Composable () -> Unit
) {
    val colors = LocalLevelUpColors.current
    val Black = colors.background
    val DarkGrey = colors.surface
    val MediumGrey = colors.surfaceVariant
    val PureWhite = colors.textPrimary
    val Slate500 = colors.textSecondary
    val LightGrey = colors.textSecondary
    val OptimalGreen = colors.accentGreen
    val SleepBlue = colors.accentBlue
    val WorkoutRed = colors.accentRed
    val TranslucentCardBg = colors.surface

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = TranslucentCardBg),
        border = BorderStroke(0.dp, Color.Transparent)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(accentColor)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = PureWhite
                )
            }
            Text(
                text = description,
                fontSize = 11.sp,
                color = Slate500,
                modifier = Modifier.padding(start = 16.dp, bottom = 12.dp)
            )
            
            content()
        }
    }
}

@Composable
fun HabitCompletionChart(stats: List<DailyStat>) {
    val colors = LocalLevelUpColors.current
    val Black = colors.background
    val DarkGrey = colors.surface
    val MediumGrey = colors.surfaceVariant
    val PureWhite = colors.textPrimary
    val Slate500 = colors.textSecondary
    val LightGrey = colors.textSecondary
    val OptimalGreen = colors.accentGreen
    val SleepBlue = colors.accentBlue
    val WorkoutRed = colors.accentRed

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .padding(top = 12.dp, bottom = 4.dp)
    ) {
        val width = size.width
        val height = size.height
        val barCount = stats.size
        
        if (barCount == 0) return@Canvas
        
        val chartBottomPadding = 24.dp.toPx()
        val chartSidePadding = 16.dp.toPx()
        val usableWidth = width - (chartSidePadding * 2)
        val usableHeight = height - chartBottomPadding - 24.dp.toPx()
        
        val spacing = 16.dp.toPx()
        val totalSpacing = spacing * (barCount - 1)
        val barWidth = (usableWidth - totalSpacing) / barCount
        
        // Draw bottom axis line (above the day labels)
        val axisY = height - chartBottomPadding
        drawLine(
            color = MediumGrey,
            start = Offset(chartSidePadding, axisY),
            end = Offset(width - chartSidePadding, axisY),
            strokeWidth = 2f
        )
        
        stats.forEachIndexed { index, stat ->
            val pct = stat.completionPercentage
            val barHeight = (pct.toFloat() / 100f) * usableHeight
            val xOffset = chartSidePadding + index * (barWidth + spacing)
            
            if (pct > 0) {
                // Draw bar
                drawRoundRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(OptimalGreen, OptimalGreen.copy(alpha = 0.5f))
                    ),
                    topLeft = Offset(xOffset, axisY - barHeight),
                    size = Size(barWidth, barHeight),
                    cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                )
            }
            
            // Draw percentage text above bar
            drawContext.canvas.nativeCanvas.apply {
                val textPaint = android.graphics.Paint().apply {
                    color = PureWhite.toArgb()
                    textSize = 10.sp.toPx()
                    typeface = android.graphics.Typeface.DEFAULT_BOLD
                    textAlign = android.graphics.Paint.Align.CENTER
                }
                drawText(
                    "$pct%",
                    xOffset + (barWidth / 2f),
                    axisY - barHeight - 6.dp.toPx(),
                    textPaint
                )
                
                // Draw Day label below axis
                val labelPaint = android.graphics.Paint().apply {
                    color = Slate500.toArgb()
                    textSize = 10.sp.toPx()
                    textAlign = android.graphics.Paint.Align.CENTER
                }
                drawText(
                    stat.dayName,
                    xOffset + (barWidth / 2f),
                    axisY + 16.dp.toPx(),
                    labelPaint
                )
            }
        }
    }
}

@Composable
fun SleepDurationChart(stats: List<DailyStat>) {
    val colors = LocalLevelUpColors.current
    val Black = colors.background
    val DarkGrey = colors.surface
    val MediumGrey = colors.surfaceVariant
    val PureWhite = colors.textPrimary
    val Slate500 = colors.textSecondary
    val LightGrey = colors.textSecondary
    val OptimalGreen = colors.accentGreen
    val SleepBlue = colors.accentBlue
    val WorkoutRed = colors.accentRed

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .padding(top = 12.dp, bottom = 4.dp)
    ) {
        val width = size.width
        val height = size.height
        val pointCount = stats.size
        
        if (pointCount == 0) return@Canvas
        
        val chartBottomPadding = 24.dp.toPx()
        val chartSidePadding = 16.dp.toPx()
        val usableWidth = width - (chartSidePadding * 2)
        val usableHeight = height - chartBottomPadding - 24.dp.toPx()
        
        val spacing = usableWidth / (pointCount - 1)
        val maxSleep = 12.0 // Max range for vertical mapping
        
        val axisY = height - chartBottomPadding
        
        val points = stats.mapIndexed { index, stat ->
            val x = chartSidePadding + index * spacing
            val ratio = (stat.sleepHours / maxSleep).coerceIn(0.0, 1.0)
            val y = axisY - (ratio.toFloat() * usableHeight)
            Offset(x, y)
        }
        
        // Draw Grid Lines (4h, 8h)
        val gridLines = listOf(4.0, 8.0)
        gridLines.forEach { target ->
            val ratio = (target / maxSleep).coerceIn(0.0, 1.0)
            val gridY = axisY - (ratio.toFloat() * usableHeight)
            drawLine(
                color = MediumGrey,
                start = Offset(chartSidePadding, gridY),
                end = Offset(width - chartSidePadding, gridY),
                strokeWidth = 1f
            )
            
            // Grid label text
            drawContext.canvas.nativeCanvas.apply {
                val labelPaint = android.graphics.Paint().apply {
                    color = Slate500.toArgb()
                    textSize = 8.sp.toPx()
                }
                drawText(
                    "${target.toInt()}h",
                    chartSidePadding + 5.dp.toPx(),
                    gridY - 4.dp.toPx(),
                    labelPaint
                )
            }
        }
        
        // Draw Curve Path
        val path = Path().apply {
            if (points.isNotEmpty()) {
                moveTo(points.first().x, points.first().y)
                for (i in 1 until points.size) {
                    lineTo(points[i].x, points[i].y)
                }
            }
        }
        
        // Draw Glow Underneath Path
        val fillPath = Path().apply {
            addPath(path)
            if (points.isNotEmpty()) {
                lineTo(points.last().x, axisY)
                lineTo(points.first().x, axisY)
                close()
            }
        }
        
        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                colors = listOf(SleepBlue.copy(alpha = 0.25f), Color.Transparent)
            )
        )
        
        drawPath(
            path = path,
            color = SleepBlue,
            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
        )
        
        // Draw nodes and hour texts
        points.forEachIndexed { index, point ->
            drawCircle(
                color = PureWhite,
                radius = 4.dp.toPx(),
                center = point
            )
            drawCircle(
                color = SleepBlue,
                radius = 2.dp.toPx(),
                center = point
            )
            
            val hours = stats[index].sleepHours
            drawContext.canvas.nativeCanvas.apply {
                val hoursPaint = android.graphics.Paint().apply {
                    color = PureWhite.toArgb()
                    textSize = 9.sp.toPx()
                    typeface = android.graphics.Typeface.DEFAULT_BOLD
                    textAlign = android.graphics.Paint.Align.CENTER
                }
                drawText(
                    if (hours > 0) String.format(Locale.getDefault(), "%.1fh", hours) else "0h",
                    point.x,
                    point.y - 8.dp.toPx(),
                    hoursPaint
                )
                
                // Draw Day text underneath
                val dayPaint = android.graphics.Paint().apply {
                    color = Slate500.toArgb()
                    textSize = 10.sp.toPx()
                    textAlign = android.graphics.Paint.Align.CENTER
                }
                drawText(
                    stats[index].dayName,
                    point.x,
                    axisY + 16.dp.toPx(),
                    dayPaint
                )
            }
        }
    }
}

@Composable
fun AddCustomHabitDialog(
    onDismiss: () -> Unit,
    onSave: (title: String, category: String, description: String) -> Unit
) {
    val colors = LocalLevelUpColors.current
    val Black = colors.background
    val DarkGrey = colors.surface
    val MediumGrey = colors.surfaceVariant
    val PureWhite = colors.textPrimary
    val Slate500 = colors.textSecondary
    val LightGrey = colors.textSecondary
    val OptimalGreen = colors.accentGreen
    val SleepBlue = colors.accentBlue
    val WorkoutRed = colors.accentRed

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Diet") }
    
    val categories = listOf("Diet", "Workout", "Sleep", "Meditation", "Evening Stretch", "General")

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = DarkGrey),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, MediumGrey)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "CREATE OPTIMAL HABIT",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp,
                    color = OptimalGreen,
                    letterSpacing = 1.sp
                )
                
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Habit Title") },
                    modifier = Modifier.fillMaxWidth().testTag("add_habit_title_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = OptimalGreen,
                        unfocusedBorderColor = MediumGrey
                    )
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth().testTag("add_habit_desc_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = OptimalGreen,
                        unfocusedBorderColor = MediumGrey
                    )
                )

                Text(
                    text = "Category",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = PureWhite
                )
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categories.forEach { cat ->
                        val isSelected = category == cat
                        Card(
                            modifier = Modifier
                                .clickable { category = cat }
                                .testTag("select_cat_$cat"),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) OptimalGreen else MediumGrey
                            )
                        ) {
                            Text(
                                text = cat.uppercase(),
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Black else PureWhite
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("CANCEL", color = PureWhite.copy(alpha = 0.6f))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (title.trim().isNotEmpty()) {
                                onSave(title, category, description)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = OptimalGreen),
                        enabled = title.trim().isNotEmpty(),
                        modifier = Modifier.testTag("save_custom_habit")
                    ) {
                        Text("SAVE HABIT", color = Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}


// ==========================================
// TAB 1: LOG ROUTINES (WORKOUT & SLEEP)
// ==========================================
@Composable
fun LogRoutinesTab(viewModel: LevelUpViewModel, selectedDate: String) {
    val colors = LocalLevelUpColors.current
    val Black = colors.background
    val DarkGrey = colors.surface
    val MediumGrey = colors.surfaceVariant
    val PureWhite = colors.textPrimary
    val Slate500 = colors.textSecondary
    val LightGrey = colors.textSecondary
    val OptimalGreen = colors.accentGreen
    val SleepBlue = colors.accentBlue
    val WorkoutRed = colors.accentRed
    val TranslucentCardBg = colors.surface

    var selectedSection by remember { mutableStateOf(0) } // 0: Workout, 1: Sleep
    val workouts by viewModel.allWorkouts.collectAsStateWithLifecycle()
    val sleepLogs by viewModel.allSleepLogs.collectAsStateWithLifecycle()

    // Filter by selected date
    val dailyWorkouts = remember(workouts, selectedDate) {
        workouts.filter { it.dateString == selectedDate }
    }
    val dailySleep = remember(sleepLogs, selectedDate) {
        sleepLogs.find { it.dateString == selectedDate }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            TabRow(
                selectedTabIndex = selectedSection,
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = PureWhite,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedSection]),
                        color = if (selectedSection == 0) WorkoutRed else SleepBlue
                    )
                }
            ) {
                Tab(
                    selected = selectedSection == 0,
                    onClick = { selectedSection = 0 },
                    text = { Text("Workout Protocol", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedSection == 1,
                    onClick = { selectedSection = 1 },
                    text = { Text("Sleep Recovery", fontWeight = FontWeight.Bold) }
                )
            }
        }

        if (selectedSection == 0) {
            item {
                WorkoutLogForm { name, sets, reps, wt, cat ->
                    viewModel.addWorkout(name, sets, reps, wt, cat)
                }
            }

            item {
                Text(
                    text = "WORKOUT SESSION RECORDED",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = WorkoutRed
                )
            }

            if (dailyWorkouts.isEmpty()) {
                item {
                    Text(
                        text = "No workouts logged for this day. Trigger physical activity to stimulate human growth hormone.",
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                        fontSize = 12.sp,
                        style = androidx.compose.ui.text.TextStyle(textAlign = TextAlign.Center),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp)
                    )
                }
            } else {
                items(dailyWorkouts, key = { it.id }) { log ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = TranslucentCardBg),
                        border = BorderStroke(0.dp, Color.Transparent)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = log.exerciseName,
                                        fontWeight = FontWeight.Bold,
                                        color = PureWhite,
                                        modifier = Modifier.weight(1f, fill = false)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Surface(color = WorkoutRed.copy(alpha = 0.15f), shape = RoundedCornerShape(4.dp)) {
                                        Text(
                                            text = log.category.uppercase(),
                                            fontSize = 8.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = WorkoutRed,
                                            maxLines = 1,
                                            softWrap = false,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "${log.sets} sets  ×  ${log.reps} reps  @  ${log.weight} lbs",
                                    fontSize = 13.sp,
                                    color = LightGrey
                                )
                            }
                            IconButton(onClick = { viewModel.deleteWorkout(log) }) {
                                Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = WorkoutRed)
                            }
                        }
                    }
                }
            }
        } else {
            item {
                SleepLogForm(dailySleep) { hrs, rating, notes ->
                    viewModel.addSleepLog(hrs, rating, notes)
                }
            }

            item {
                Text(
                    text = "SLEEP LOG HISTORY",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = SleepBlue
                )
            }

            if (dailySleep == null) {
                item {
                    Text(
                        text = "No sleep recorded for this date. 8 hours of deep restorative sleep is critical to unlock optimal cognitive clarity.",
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp)
                    )
                }
            } else {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = TranslucentCardBg),
                        border = BorderStroke(0.dp, Color.Transparent)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = "${dailySleep.durationHours} Hours", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = SleepBlue)
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Surface(color = OptimalGreen.copy(alpha = 0.15f), shape = RoundedCornerShape(4.dp)) {
                                        Text(
                                            text = "QUALITY: ${dailySleep.qualityRating}/10",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = OptimalGreen,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                if (dailySleep.notes.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(text = dailySleep.notes, fontSize = 12.sp, color = LightGrey)
                                }
                            }
                            IconButton(onClick = { viewModel.deleteSleepLog(dailySleep) }) {
                                Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = WorkoutRed)
                            }
                        }
                    }
                }
            }
        }
        
        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun WorkoutLogForm(onAdd: (String, Int, Int, Double, String) -> Unit) {
    val colors = LocalLevelUpColors.current
    val Black = colors.background
    val DarkGrey = colors.surface
    val MediumGrey = colors.surfaceVariant
    val PureWhite = colors.textPrimary
    val Slate500 = colors.textSecondary
    val LightGrey = colors.textSecondary
    val OptimalGreen = colors.accentGreen
    val SleepBlue = colors.accentBlue
    val WorkoutRed = colors.accentRed
    val TranslucentCardBg = colors.surface

    var name by remember { mutableStateOf("") }
    var sets by remember { mutableStateOf("3") }
    var reps by remember { mutableStateOf("10") }
    var weight by remember { mutableStateOf("135") }
    var category by remember { mutableStateOf("Strength") }
    
    val categories = listOf("Strength", "Cardio", "Stretch")

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = TranslucentCardBg),
        border = BorderStroke(0.dp, Color.Transparent)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "LOG NEW WORKOUT SET",
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = WorkoutRed,
                letterSpacing = 0.5.sp
            )
            
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Exercise Name") },
                modifier = Modifier.fillMaxWidth().testTag("workout_name_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = WorkoutRed,
                    unfocusedBorderColor = MediumGrey
                )
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = sets,
                    onValueChange = { sets = it },
                    label = { Text("Sets") },
                    modifier = Modifier.weight(1f).testTag("workout_sets_input"),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = WorkoutRed, unfocusedBorderColor = MediumGrey)
                )
                OutlinedTextField(
                    value = reps,
                    onValueChange = { reps = it },
                    label = { Text("Reps") },
                    modifier = Modifier.weight(1f).testTag("workout_reps_input"),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = WorkoutRed, unfocusedBorderColor = MediumGrey)
                )
                OutlinedTextField(
                    value = weight,
                    onValueChange = { weight = it },
                    label = { Text("Weight (lbs)") },
                    modifier = Modifier.weight(1.2f).testTag("workout_weight_input"),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = WorkoutRed, unfocusedBorderColor = MediumGrey)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                categories.forEach { cat ->
                    val isSel = category == cat
                    Card(
                        modifier = Modifier
                            .clickable { category = cat }
                            .weight(1f),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSel) WorkoutRed else MediumGrey
                        )
                    ) {
                        Text(
                            text = cat,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            textAlign = TextAlign.Center,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSel) Black else PureWhite
                        )
                    }
                }
            }

            Button(
                onClick = {
                    if (name.trim().isNotEmpty()) {
                        val s = sets.toIntOrNull() ?: 3
                        val r = reps.toIntOrNull() ?: 10
                        val w = weight.toDoubleOrNull() ?: 0.0
                        onAdd(name, s, r, w, category)
                        name = ""
                    }
                },
                modifier = Modifier.fillMaxWidth().testTag("add_workout_submit"),
                colors = ButtonDefaults.buttonColors(containerColor = WorkoutRed),
                enabled = name.trim().isNotEmpty()
            ) {
                Text("ADD PHYSICAL RECORD", color = Black, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun SleepLogForm(existing: SleepLog?, onAdd: (Double, Int, String) -> Unit) {
    val colors = LocalLevelUpColors.current
    val Black = colors.background
    val DarkGrey = colors.surface
    val MediumGrey = colors.surfaceVariant
    val PureWhite = colors.textPrimary
    val Slate500 = colors.textSecondary
    val LightGrey = colors.textSecondary
    val OptimalGreen = colors.accentGreen
    val SleepBlue = colors.accentBlue
    val WorkoutRed = colors.accentRed
    val TranslucentCardBg = colors.surface

    var hours by remember { mutableStateOf(existing?.durationHours ?: 8.0) }
    var quality by remember { mutableStateOf(existing?.qualityRating ?: 7) }
    var notes by remember { mutableStateOf(existing?.notes ?: "") }
    
    LaunchedEffect(existing) {
        if (existing != null) {
            hours = existing.durationHours
            quality = existing.qualityRating
            notes = existing.notes
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = TranslucentCardBg),
        border = BorderStroke(0.dp, Color.Transparent)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "RECORD DAILY REST SCHEDULE",
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = SleepBlue,
                letterSpacing = 0.5.sp
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Sleep Duration: ${String.format(Locale.getDefault(), "%.1f", hours)} Hours", fontSize = 14.sp)
            }
            Slider(
                value = hours.toFloat(),
                onValueChange = { hours = it.toDouble() },
                valueRange = 0f..16f,
                steps = 32,
                colors = SliderDefaults.colors(thumbColor = SleepBlue, activeTrackColor = SleepBlue)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Sleep Quality: $quality/10", fontSize = 14.sp)
            }
            Slider(
                value = quality.toFloat(),
                onValueChange = { quality = it.toInt() },
                valueRange = 1f..10f,
                steps = 8,
                colors = SliderDefaults.colors(thumbColor = SleepBlue, activeTrackColor = SleepBlue)
            )

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Quality notes (e.g. cold room, heavy eyes)") },
                modifier = Modifier.fillMaxWidth().testTag("sleep_notes_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SleepBlue,
                    unfocusedBorderColor = MediumGrey
                )
            )

            Button(
                onClick = {
                    onAdd(hours, quality, notes)
                    notes = ""
                },
                modifier = Modifier.fillMaxWidth().testTag("add_sleep_submit"),
                colors = ButtonDefaults.buttonColors(containerColor = SleepBlue)
            ) {
                Text("RECORD Rest RATIO", color = Black, fontWeight = FontWeight.Bold)
            }
        }
    }
}


// ==========================================
// TAB 2: EVENING STRETCHING TIMER
// ==========================================
@Composable
fun EveningStretchingTab(viewModel: LevelUpViewModel) {
    val colors = LocalLevelUpColors.current
    val Black = colors.background
    val DarkGrey = colors.surface
    val MediumGrey = colors.surfaceVariant
    val PureWhite = colors.textPrimary
    val Slate500 = colors.textSecondary
    val LightGrey = colors.textSecondary
    val OptimalGreen = colors.accentGreen
    val SleepBlue = colors.accentBlue
    val WorkoutRed = colors.accentRed
    val TranslucentCardBg = colors.surface

    val currentStretchIndex by viewModel.currentStretchIndex.collectAsStateWithLifecycle()
    val secondsLeft by viewModel.stretchTimerSeconds.collectAsStateWithLifecycle()
    val timerActive by viewModel.stretchTimerActive.collectAsStateWithLifecycle()

    val currentStretch = viewModel.stretchesList[currentStretchIndex]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = TranslucentCardBg),
            border = BorderStroke(0.dp, Color.Transparent)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "EVENING GROWTH STRETCHES",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 11.sp,
                    color = WorkoutRed,
                    letterSpacing = 2.sp
                )
                Spacer(modifier = Modifier.height(14.dp))
                
                // Big beautiful circular progress timer
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(160.dp)
                ) {
                    val progressFraction = secondsLeft.toFloat() / currentStretch.durationSeconds.toFloat()
                    CircularProgressIndicator(
                        progress = { progressFraction },
                        modifier = Modifier.fillMaxSize(),
                        color = WorkoutRed,
                        strokeWidth = 8.dp,
                        trackColor = MediumGrey,
                        strokeCap = StrokeCap.Round,
                    )
                    
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = String.format(Locale.getDefault(), "%02d:%02d", secondsLeft / 60, secondsLeft % 60),
                            fontSize = 32.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = PureWhite
                        )
                        Text(
                            text = "STRETCH ${currentStretchIndex + 1}/${viewModel.stretchesList.size}",
                            fontSize = 11.sp,
                            color = LightGrey,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = currentStretch.name,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp,
                    color = PureWhite
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = currentStretch.description,
                    fontSize = 13.sp,
                    color = LightGrey,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp
                )
                
                Spacer(modifier = Modifier.height(20.dp))
                
                // Control Buttons
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { viewModel.resetStretchTimer() },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(MediumGrey)
                            .testTag("reset_stretch")
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Reset", tint = PureWhite)
                    }
                    
                    Button(
                        onClick = {
                            if (timerActive) viewModel.pauseStretchTimer() else viewModel.startStretchTimer()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = WorkoutRed),
                        modifier = Modifier
                            .height(48.dp)
                            .widthIn(min = 120.dp)
                            .testTag("toggle_stretch_timer"),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Icon(
                            imageVector = if (timerActive) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = "Control",
                            tint = Black
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (timerActive) "PAUSE" else "START",
                            fontWeight = FontWeight.ExtraBold,
                            color = Black
                        )
                    }

                    IconButton(
                        onClick = { viewModel.skipStretch() },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(MediumGrey)
                            .testTag("skip_stretch")
                    ) {
                        Icon(Icons.Default.SkipNext, contentDescription = "Skip", tint = PureWhite)
                    }
                }
            }
        }

        // Checklist of Stretches
        Text(
            text = "GROWTH STRETCH ROTATION GUIDE",
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            color = WorkoutRed,
            letterSpacing = 1.sp,
            modifier = Modifier.align(Alignment.Start)
        )

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            viewModel.stretchesList.forEachIndexed { idx, stretch ->
                val isActive = idx == currentStretchIndex
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isActive) WorkoutRed.copy(alpha = 0.1f) else TranslucentCardBg
                    ),
                    border = BorderStroke(0.dp, Color.Transparent)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(if (isActive) WorkoutRed else MediumGrey)
                        ) {
                            Text(
                                text = "${idx + 1}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isActive) Black else PureWhite
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = stretch.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = if (isActive) WorkoutRed else PureWhite
                            )
                            Text(
                                text = "${stretch.durationSeconds}s duration",
                                fontSize = 11.sp,
                                color = LightGrey
                            )
                        }
                    }
                }
            }
        }
    }
}


// ==========================================
// TAB 3: GUIDED MEDITATION TAB
// ==========================================
@Composable
fun GuidedMeditationTab(viewModel: LevelUpViewModel) {
    val colors = LocalLevelUpColors.current
    val Black = colors.background
    val DarkGrey = colors.surface
    val MediumGrey = colors.surfaceVariant
    val PureWhite = colors.textPrimary
    val Slate500 = colors.textSecondary
    val LightGrey = colors.textSecondary
    val OptimalGreen = colors.accentGreen
    val SleepBlue = colors.accentBlue
    val WorkoutRed = colors.accentRed
    val TranslucentCardBg = colors.surface

    val secondsLeft by viewModel.meditationTimerSeconds.collectAsStateWithLifecycle()
    val timerActive by viewModel.meditationTimerActive.collectAsStateWithLifecycle()
    val breathingStage by viewModel.breathingStage.collectAsStateWithLifecycle()
    val breathingScale by viewModel.breathingScale.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = TranslucentCardBg),
            border = BorderStroke(0.dp, Color.Transparent)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "COGNITIVE CLARITY MEDITATION",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 11.sp,
                    color = SleepBlue,
                    letterSpacing = 1.5.sp
                )
                Spacer(modifier = Modifier.height(20.dp))
                
                // Pulsing Diaphragmatic Breathing Circle
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(180.dp)
                        .drawBehind {
                            // Pulsing glowing background ring
                            drawCircle(
                                color = SleepBlue.copy(alpha = 0.15f),
                                radius = (80.dp.toPx() * breathingScale)
                            )
                            // Outer solid guideline
                            drawCircle(
                                color = SleepBlue.copy(alpha = 0.4f),
                                radius = 70.dp.toPx(),
                                style = Stroke(width = 1.dp.toPx())
                            )
                        }
                ) {
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .clip(CircleShape)
                            .background(SleepBlue)
                    )
                    
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = breathingStage.uppercase(),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Black
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    text = String.format(Locale.getDefault(), "%02d:%02d", secondsLeft / 60, secondsLeft % 60),
                    fontSize = 40.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = PureWhite
                )
                Text(
                    text = "Follow the pulsing cycle: 4s Inhale, 4s Hold, 4s Exhale, 4s Hold",
                    fontSize = 11.sp,
                    color = LightGrey,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
                
                Spacer(modifier = Modifier.height(20.dp))
                
                // Presets Selection
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    listOf(1, 5, 10).forEach { mins ->
                        Card(
                            modifier = Modifier
                                .clickable { viewModel.startMeditationTimer(mins) }
                                .testTag("meditation_preset_$mins"),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (secondsLeft == mins * 60) SleepBlue else Color(0xFF1E1E1E)
                            ),
                            border = BorderStroke(0.dp, Color.Transparent)
                        ) {
                            Text(
                                text = "$mins MIN",
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (secondsLeft == mins * 60) Black else PureWhite
                            )
                        }
                    }
                }

                // Controls
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { viewModel.resetMeditationTimer() },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(MediumGrey)
                            .testTag("reset_meditation")
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Reset", tint = PureWhite)
                    }
                    
                    Button(
                        onClick = {
                            if (timerActive) viewModel.pauseMeditationTimer() else viewModel.startMeditationTimer(secondsLeft / 60)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SleepBlue),
                        modifier = Modifier
                            .height(48.dp)
                            .widthIn(min = 140.dp)
                            .testTag("toggle_meditation_timer"),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Icon(
                            imageVector = if (timerActive) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = "Control",
                            tint = PureWhite
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (timerActive) "PAUSE COACH" else "START COACH",
                            fontWeight = FontWeight.ExtraBold,
                            color = PureWhite
                        )
                    }
                }
            }
        }

        // Cognitive benefits card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = TranslucentCardBg),
            border = BorderStroke(0.dp, Color.Transparent)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Insights, contentDescription = "Clarity", tint = SleepBlue)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Cognitive Optimization", fontWeight = FontWeight.Bold, color = PureWhite)
                }
                Text(
                    text = "Controlled diaphragmatic box breathing has been scientifically proven to boost mental clarity, balance the autonomic nervous system, decrease pre-frontal cortex cortisol, and triggers neuroplastic recovery.",
                    fontSize = 12.sp,
                    color = LightGrey,
                    lineHeight = 16.sp
                )
            }
        }
    }
}


// ==========================================
// TAB 4: AI COACH TERMINAL
// ==========================================
@Composable
fun AiAdvisorTab(viewModel: LevelUpViewModel) {
    val colors = LocalLevelUpColors.current
    val Black = colors.background
    val DarkGrey = colors.surface
    val MediumGrey = colors.surfaceVariant
    val PureWhite = colors.textPrimary
    val Slate500 = colors.textSecondary
    val LightGrey = colors.textSecondary
    val OptimalGreen = colors.accentGreen
    val SleepBlue = colors.accentBlue
    val WorkoutRed = colors.accentRed
    val TranslucentCardBg = colors.surface

    var query by remember { mutableStateOf("") }
    val loading by viewModel.aiQueryLoading.collectAsStateWithLifecycle()
    val response by viewModel.aiResponse.collectAsStateWithLifecycle()

    val presets = listOf(
        Pair("Hair Density Diet", "Hair Density Nutrition Intake Guide"),
        Pair("Facial Symmetry Protocol", "Aesthetic Facial Symmetry Exercises"),
        Pair("Height Stretching after 18", "Height Growth stretches & Evening schedules"),
        Pair("Mental Clarity Routine", "Daily Cognitive Clarity scheduling")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "APEX AI BIO-COACH",
            fontWeight = FontWeight.ExtraBold,
            fontSize = 14.sp,
            letterSpacing = 1.sp,
            color = OptimalGreen
        )
        Text(
            text = "Generate high-density plans for hair density, facial symmetry, height stretching, and nutrition",
            fontSize = 12.sp,
            color = LightGrey
        )

        // Presets Grid
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            presets.forEach { preset ->
                Card(
                    modifier = Modifier
                        .clickable { viewModel.getGrowthAdvice(preset.first) }
                        .testTag("ai_preset_${preset.first.replace(" ", "_")}"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = TranslucentCardBg),
                    border = BorderStroke(0.dp, Color.Transparent)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = preset.first.uppercase(),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = OptimalGreen
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Generate Guide",
                            fontSize = 10.sp,
                            color = LightGrey
                        )
                    }
                }
            }
        }

        // Custom Query Field
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = TranslucentCardBg),
            border = BorderStroke(0.dp, Color.Transparent)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    label = { Text("Ask anything regarding habits or growth...") },
                    modifier = Modifier.fillMaxWidth().testTag("ai_custom_query_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = OptimalGreen,
                        unfocusedBorderColor = MediumGrey
                    )
                )

                Button(
                    onClick = {
                        if (query.trim().isNotEmpty()) {
                            viewModel.getGrowthAdvice(query)
                            query = ""
                        }
                    },
                    modifier = Modifier.fillMaxWidth().testTag("ai_submit_query"),
                    colors = ButtonDefaults.buttonColors(containerColor = OptimalGreen),
                    enabled = query.trim().isNotEmpty()
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = "Query", tint = Black)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("CONSULT COACH", color = Black, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Results Terminal Card
        if (loading || response != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = TranslucentCardBg),
                border = BorderStroke(0.dp, Color.Transparent)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "APEX SYSTEM ANALYSIS",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = OptimalGreen,
                            letterSpacing = 1.sp
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    if (response != null && !loading) {
                        Text(
                            text = response ?: "",
                            color = PureWhite,
                            fontSize = 13.sp,
                            lineHeight = 18.sp,
                            modifier = Modifier.testTag("ai_response_text")
                        )
                    }
                }
            }
        }
        
        if (loading) {
            androidx.compose.ui.window.Dialog(
                onDismissRequest = {},
                properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
            ) {
                LevelUpLoadingScreen()
            }
        }
    }
}
