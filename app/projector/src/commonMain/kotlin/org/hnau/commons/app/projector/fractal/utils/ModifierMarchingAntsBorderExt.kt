package org.hnau.commons.app.projector.fractal.utils

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.addOutline
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import org.hnau.commons.app.projector.fractal.utils.LocalDistance
import org.hnau.commons.app.projector.fractal.utils.size.units
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.seconds

@Composable
fun Modifier.fractalDashBorder(
    color: Color,
    shape: Shape,
    strokeWidth: Dp = LocalDistance.current.units.borderWidth,
    dashLength: Dp = LocalDistance.current.units.iconSize / 2,
    gapLength: Dp = LocalDistance.current.units.iconSize / 3,
): Modifier {
    val infiniteTransition = rememberInfiniteTransition(label = "DashTransition")
    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1.seconds.inWholeMilliseconds.toInt(), easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "DashProgress"
    )

    return this.drawWithCache {
        val outline = shape.createOutline(size, layoutDirection, this)
        val path = Path().apply { addOutline(outline) }

        val pathMeasure = PathMeasure()
        pathMeasure.setPath(path, false)
        val perimeter = pathMeasure.length

        val patternLength = (dashLength + gapLength).toPx()
        val count = (perimeter / patternLength).roundToInt().coerceAtLeast(1)
        val actualStep = perimeter / count

        val dashRatio = dashLength / (dashLength + gapLength)
        val finalDash = actualStep * dashRatio
        val finalGap = actualStep * (1f - dashRatio)

        onDrawBehind {
            val stroke = Stroke(
                width = strokeWidth.toPx(),
                pathEffect = PathEffect.dashPathEffect(
                    intervals = floatArrayOf(finalDash, finalGap),
                    phase = progress * actualStep
                )
            )

            drawPath(path, color, style = stroke)
        }
    }
}