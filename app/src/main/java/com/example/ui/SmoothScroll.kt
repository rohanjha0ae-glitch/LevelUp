package com.example.ui

import androidx.compose.animation.core.AnimationState
import androidx.compose.animation.core.animateDecay
import androidx.compose.animation.core.exponentialDecay
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.compositionLocalOf
import kotlin.math.abs

val LocalScrollableFlingBehavior = compositionLocalOf<FlingBehavior?> { null }

@Composable
fun rememberSmoothFlingBehavior(): FlingBehavior {
    val decay = remember { exponentialDecay<Float>(frictionMultiplier = 0.8f) }
    return remember(decay) {
        object : FlingBehavior {
            override suspend fun ScrollScope.performFling(initialVelocity: Float): Float {
                var velocityLeft = initialVelocity
                var lastValue = 0f
                AnimationState(
                    initialValue = 0f,
                    initialVelocity = initialVelocity,
                ).animateDecay(decay) {
                    val delta = value - lastValue
                    val consumed = scrollBy(delta)
                    lastValue = value
                    velocityLeft = this.velocity
                    // Stop animation if we couldn't consume all
                    if (abs(delta - consumed) > 0.5f) {
                        this.cancelAnimation()
                    }
                }
                return velocityLeft
            }
        }
    }
}
