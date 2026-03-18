package org.hnau.commons.app.projector.uikit.transition

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import kotlin.math.absoluteValue
import kotlin.math.sign
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

fun <T, C : Comparable<C>> getTransitionSpecForSlideByCompare(
    duration: Duration = AnimationDuration,
    orientation: SlideOrientation,
    offsetFactor: Float = 0.25f,
    extractComparable: (T) -> C,
): AnimatedContentTransitionScope<T>.() -> ContentTransform = getTransitionSpecForSlide(
    duration = duration,
    orientation = orientation,
    slideCoefficientProvider = {

        extractComparable(targetState)
            .compareTo(extractComparable(initialState))
            .sign
            .times(offsetFactor)
    }
)

fun <T> getTransitionSpecForSlide(
    duration: Duration = AnimationDuration,
    orientation: SlideOrientation,
    slideCoefficientProvider: AnimatedContentTransitionScope<T>.() -> Number,
): AnimatedContentTransitionScope<T>.() -> ContentTransform {
    val durationMillis = duration.inWholeMilliseconds.toInt()
    val animationSpecInt = tween<IntOffset>(durationMillis)
    val animationSpecFloat = tween<Float>(durationMillis)
    return {

        val slideCoefficient = slideCoefficientProvider().toFloat()
        val fade = slideCoefficient.absoluteValue < 1
        var enterTransition = slideIn(
            initialOffset = { it.toOffset(orientation) * slideCoefficient },
            animationSpec = animationSpecInt
        )
        if (fade) {
            enterTransition += fadeIn(
                animationSpec = animationSpecFloat
            )
        }
        var exitTransition = slideOut(
            targetOffset = { it.toOffset(orientation) * (-slideCoefficient) },
            animationSpec = animationSpecInt
        )
        if (fade) {
            exitTransition += fadeOut(
                animationSpec = animationSpecFloat
            )
        }
        val result = enterTransition togetherWith exitTransition
        result.targetContentZIndex = slideCoefficient.sign
        result
    }
}

private fun IntSize.toOffset(
    orientation: SlideOrientation,
): IntOffset = orientation.fold(
    ifHorizontal = { IntOffset(width, 0) },
    ifVertical = { IntOffset(0, height) },
)

private val AnimationDuration = 0.25.seconds