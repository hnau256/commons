package org.hnau.commons.app.projector.uikit.transition

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkOut
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import org.hnau.commons.kotlin.foldNullable
import androidx.compose.runtime.remember as rememberInComposer

object TransitionSpec {

    @Composable
    fun <S> rememberCenter(): AnimatedContentTransitionScope<S>.() -> ContentTransform {
        val layoutDirection = LocalLayoutDirection.current
        return rememberInComposer(layoutDirection) {
            center(
                layoutDirection = layoutDirection,
            )
        }
    }

    fun <S> center(
        layoutDirection: LayoutDirection,
    ): AnimatedContentTransitionScope<S>.() -> ContentTransform = create(
        showAlignment = Alignment.Center,
        hideAlignment = Alignment.Center,
        layoutDirection = layoutDirection,
    )

    @Composable
    fun <S> remember(
        showAlignment: Alignment?,
        hideAlignment: Alignment?,
    ): AnimatedContentTransitionScope<S>.() -> ContentTransform {
        val layoutDirection = LocalLayoutDirection.current
        return rememberInComposer(
            showAlignment,
            hideAlignment,
            layoutDirection,
        ) {
            create(
                showAlignment = showAlignment,
                hideAlignment = hideAlignment,
                layoutDirection = layoutDirection,
            )
        }
    }

    fun <S> create(
        showAlignment: Alignment?,
        hideAlignment: Alignment?,
        layoutDirection: LayoutDirection,
    ): AnimatedContentTransitionScope<S>.() -> ContentTransform {

        val enterDurationMillis = 220
        val exitDurationMillis = 90

        val enterFloatSpec = tween<Float>(
            durationMillis = enterDurationMillis,
            delayMillis = exitDurationMillis,
        )

        var enter = fadeIn(enterFloatSpec) + scaleIn(
            initialScale = 0.92f,
            animationSpec = enterFloatSpec,
            transformOrigin = showAlignment.toTransformOrigin(
                layoutDirection = layoutDirection,
            )
        )

        if (showAlignment != null) {
            enter += expandIn(
                animationSpec = tween(exitDurationMillis + enterDurationMillis),
                expandFrom = showAlignment,
                clip = false,
            )
        }

        val exitFloatSpec = tween<Float>(
            durationMillis = exitDurationMillis,
        )

        var exit = fadeOut(exitFloatSpec) + scaleOut(
            targetScale = 0.8f,
            animationSpec = exitFloatSpec,
            transformOrigin = hideAlignment.toTransformOrigin(
                layoutDirection = layoutDirection,
            )
        )

        if (hideAlignment != null) {
            exit += shrinkOut(
                animationSpec = tween(exitDurationMillis),
                shrinkTowards = hideAlignment,
                clip = false,
            )
        }

        return { enter togetherWith exit }
    }

    private fun Alignment?.toTransformOrigin(
        layoutDirection: LayoutDirection,
    ): TransformOrigin = this.foldNullable(
        ifNull = { TransformOrigin.Center },
        ifNotNull = { alignment ->
            when (alignment) {
                is BiasAlignment -> TransformOrigin(
                    pivotFractionX = (alignment.horizontalBias + 1f) / 2f,
                    pivotFractionY = (alignment.verticalBias + 1f) / 2f,
                )

                else -> {
                    val space = IntSize(1000, 1000)
                    val offset = alignment.align(IntSize.Zero, space, layoutDirection)
                    TransformOrigin(
                        pivotFractionX = offset.x / 1000f,
                        pivotFractionY = offset.y / 1000f,
                    )
                }
            }
        }
    )
}