package com.example.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun LevelUpLoadingScreen(modifier: Modifier = Modifier) {
    val isDark = isSystemInDarkTheme()
    
    val bgColor = if (isDark) Color(0xFF020617) else Color(0xFFF8FAFC)
    val textPrimary = if (isDark) Color(0xFFFFFFFF) else Color(0xFF0F172A)
    val textSecondary = if (isDark) Color(0xFF94A3B8) else Color(0xFF475569)
    val accentColor = if (isDark) Color(0xFF2DD4BF) else Color(0xFF2563EB)
    
    val ringMental = if (isDark) Color(0xFF60A5FA) else Color(0xFF3B82F6)
    val ringDiet = if (isDark) Color(0xFF2DD4BF) else Color(0xFF14B8A6)
    val ringSleep = if (isDark) Color(0xFF818CF8) else Color(0xFF6366F1)

    val tips = listOf(
        "Calibrating concentric stability rings...",
        "Apex AI is analyzing your growth trajectory...",
        "Preparing evening spinal decompression...",
        "Optimizing box breathing engines...",
        "Syncing your human perfection data..."
    )

    var tipIndex by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(2500)
            tipIndex = (tipIndex + 1) % tips.size
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(bgColor),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Rings Wrapper
            Box(
                modifier = Modifier
                    .size(256.dp),
                contentAlignment = Alignment.Center
            ) {
                // Outer ring
                SpinningRing(
                    color = ringMental,
                    size = 256.dp,
                    durationMillis = 2000,
                    clockwise = true,
                    strokeWidth = 12f,
                    sweepAngle = 180f
                )
                // Middle ring
                SpinningRing(
                    color = ringDiet,
                    size = 208.dp, // 256 - 48
                    durationMillis = 3000,
                    clockwise = false,
                    strokeWidth = 12f,
                    sweepAngle = 180f
                )
                // Inner ring
                SpinningRing(
                    color = ringSleep,
                    size = 160.dp, // 208 - 48
                    durationMillis = 1500,
                    clockwise = true,
                    strokeWidth = 12f,
                    sweepAngle = 180f
                )

                // Center logo
                val infiniteTransition = rememberInfiniteTransition(label = "pulse")
                val alpha by infiniteTransition.animateFloat(
                    initialValue = 0.5f,
                    targetValue = 1f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(1000, easing = LinearEasing),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "alpha"
                )

                Text(
                    text = "LU",
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Black,
                    color = textPrimary,
                    modifier = Modifier.alpha(alpha)
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Text Container
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Level Up",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Black,
                    color = textPrimary,
                    letterSpacing = (-0.5).sp
                )
                Text(
                    text = "HUMAN PERFECTION ENGINE",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = accentColor,
                    letterSpacing = 4.sp,
                    modifier = Modifier.padding(top = 8.dp, bottom = 32.dp)
                )

                // Tip container
                Box(
                    modifier = Modifier.height(48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    val infiniteTransition = rememberInfiniteTransition(label = "pulse_tip")
                    val tipAlpha by infiniteTransition.animateFloat(
                        initialValue = 0.5f,
                        targetValue = 1f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(1250, easing = LinearEasing),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "tip_alpha"
                    )
                    Text(
                        text = tips[tipIndex],
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = textSecondary,
                        modifier = Modifier.alpha(tipAlpha)
                    )
                }
            }
        }
    }
}

@Composable
fun SpinningRing(
    color: Color,
    size: androidx.compose.ui.unit.Dp,
    durationMillis: Int,
    clockwise: Boolean,
    strokeWidth: Float,
    sweepAngle: Float
) {
    val infiniteTransition = rememberInfiniteTransition(label = "spin")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = if (clockwise) 360f else -360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Canvas(
        modifier = Modifier
            .size(size)
            .rotate(rotation)
    ) {
        drawArc(
            color = color,
            startAngle = 0f,
            sweepAngle = sweepAngle,
            useCenter = false,
            style = Stroke(width = strokeWidth)
        )
    }
}
