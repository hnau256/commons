package org.hnau.commons.app.projector.uikit.transition

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.IntSize
import org.hnau.commons.kotlin.foldBoolean

object TransitionSpec {

    fun <S> both(): AnimatedContentTransitionScope<S>.() -> ContentTransform = {
        val enter = enterFadeIn + scaleIn(
            initialScale = 0.92f,
            animationSpec = enterFloatSpec
        )
        val exit = exitFadeOut + scaleOut(
            targetScale = 0.8f,
            animationSpec = exitFloatSpec,
        )
        enter togetherWith exit
    }

    fun <S> crossfade(): AnimatedContentTransitionScope<S>.() -> ContentTransform = {
        enterFadeIn togetherWith exitFadeOut
    }

    fun <S> vertical(): AnimatedContentTransitionScope<S>.() -> ContentTransform =
        { buildSlideTransform(Orientation.Vertical) }

    fun <S> horizontal(): AnimatedContentTransitionScope<S>.() -> ContentTransform =
        { buildSlideTransform(Orientation.Horizontal) }

    private fun <S> AnimatedContentTransitionScope<S>.buildSlideTransform(
        orientation: Orientation
    ): ContentTransform {

        val enter = enterFadeIn + orientation.fold(
            ifHorizontal = {
                expandHorizontally(
                    animationSpec = enterIntSizeSpec,
                    expandFrom = Alignment.CenterHorizontally,
                    clip = false,
                )
            },
            ifVertical = {
                expandVertically(
                    animationSpec = enterIntSizeSpec,
                    expandFrom = Alignment.CenterVertically,
                    clip = false,
                )
            }
        )

        val exit = exitFadeOut + orientation.fold(
            ifHorizontal = {
                shrinkHorizontally(
                    animationSpec = exitIntSizeSpec,
                    shrinkTowards = Alignment.CenterHorizontally,
                    clip = false,
                )
            },
            ifVertical = {
                shrinkVertically(
                    animationSpec = exitIntSizeSpec,
                    shrinkTowards = Alignment.CenterVertically,
                    clip = false,
                )
            }
        )

        return enter togetherWith exit using createSizeTransform(orientation)
    }

    private fun createSizeTransform(
        orientation: Orientation,
    ): SizeTransform = SizeTransform(
        clip = false,
    ) { initialSize, targetSize ->

        val isAppearing = orientation.fold(
            ifHorizontal = { initialSize.width == 0 },
            ifVertical = { initialSize.height == 0 }
        )

        val isDisappearing = !isAppearing && orientation.fold(
            ifHorizontal = { targetSize.width == 0 },
            ifVertical = { targetSize.height == 0 }
        )

        when {
            isAppearing -> keyframes {
                durationMillis = EnterDurationMillis
                orientation.fold(
                    ifHorizontal = { IntSize(initialSize.width, targetSize.height) },
                    ifVertical = { IntSize(targetSize.width, initialSize.height) }
                ) at 0 using FastOutSlowInEasing
            }

            isDisappearing -> keyframes {
                durationMillis = ExitDurationMillis
                orientation.fold(
                    ifHorizontal = { IntSize(targetSize.width, initialSize.height) },
                    ifVertical = { IntSize(initialSize.width, targetSize.height) }
                ) at ExitDurationMillis using FastOutSlowInEasing
            }

            else -> {
                orientation
                    .fold(
                        ifHorizontal = { targetSize.width > initialSize.width },
                        ifVertical = { targetSize.height > initialSize.height }
                    )
                    .foldBoolean(
                        ifTrue = { enterIntSizeSpec },
                        ifFalse = { exitIntSizeSpec },
                    )
            }
        }
    }


    private const val EnterDurationMillis = 250
    private const val ExitDurationMillis = 200

    private val enterIntSizeSpec = tween<IntSize>(EnterDurationMillis)
    private val exitIntSizeSpec = tween<IntSize>(ExitDurationMillis)

    private val enterFloatSpec = tween<Float>(EnterDurationMillis)
    private val exitFloatSpec = tween<Float>(ExitDurationMillis)

    private val enterFadeIn = fadeIn(enterFloatSpec)
    private val exitFadeOut = fadeOut(exitFloatSpec)
}