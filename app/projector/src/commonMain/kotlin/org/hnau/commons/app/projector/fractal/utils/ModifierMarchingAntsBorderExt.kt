package org.hnau.commons.app.projector.fractal.utils

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlin.time.Duration.Companion.seconds

@Composable
fun createMarchingAntsStroke(): Stroke {

    val scale = localContentScale
    val density = LocalDensity.current
    val intervals = remember(scale, density) {
        listOf(12, 8)
            .map { with(density) { it.dp.scale(scale, step = 1.dp).toPx() } }
            .toFloatArray()
    }

    val phase: Float by rememberInfiniteTransition(label = "DashTransition").animateFloat(
        initialValue = intervals.fold(0f, Float::plus),
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1.seconds.inWholeMilliseconds.toInt(),
                easing = LinearEasing,
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "DashPhase"
    )

    return Stroke(
        width = with(density) { localBorderWidth.toPx() },
        pathEffect = PathEffect.dashPathEffect(
            intervals = intervals,
            phase = phase
        )
    )
}

@Composable
fun Modifier.marchingAntsRoundRectBorder(
    color: Color,
): Modifier {
    val stroke = createMarchingAntsStroke()
    val shrinkage = localBorderWidth / 2
    val cornerRadius = localCornerRadius - shrinkage
    return drawBehind {
        val shrinkage = shrinkage.toPx()
        drawRoundRect(
            cornerRadius = CornerRadius(cornerRadius.toPx()),
            topLeft = Offset(shrinkage, shrinkage),
            size = Size(
                width = size.width - shrinkage * 2,
                height = size.height - shrinkage * 2,
            ),
            style = stroke,
            color = color,
        )
    }
}