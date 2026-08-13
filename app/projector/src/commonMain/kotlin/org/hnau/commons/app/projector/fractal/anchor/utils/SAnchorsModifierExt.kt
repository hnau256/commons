package org.hnau.commons.app.projector.fractal.anchor.utils

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import arrow.core.NonEmptyList
import org.hnau.commons.app.projector.fractal.anchor.Along
import org.hnau.commons.app.projector.fractal.anchor.Anchor
import org.hnau.commons.app.projector.fractal.anchor.Position
import org.hnau.commons.app.projector.uikit.line.ext.Offset
import org.hnau.commons.app.projector.uikit.line.ext.along
import org.hnau.commons.app.projector.utils.Orientation
import kotlin.math.floor
import kotlin.time.Clock


internal fun Modifier.sAnchorsClipToCursorRect(
    getAnchorRect: () -> Rect,
    getCursorRect: () -> Rect,
    cornerRadiusPx: Float,
    clipOp: ClipOp,
): Modifier = drawWithContent {

    val anchorRect = getAnchorRect()
    val cursorRect = getCursorRect()

    val clipLeft = (cursorRect.left - anchorRect.left).coerceIn(0f, size.width)
    val clipRight = (cursorRect.right - anchorRect.left).coerceIn(0f, size.width)

    val path = Path().apply {
        addRoundRect(
            RoundRect(
                left = clipLeft,
                top = 0f,
                right = clipRight.coerceAtLeast(clipLeft),
                bottom = size.height,
                radiusX = cornerRadiusPx,
                radiusY = cornerRadiusPx,
            )
        )
    }
    clipPath(
        path = path,
        clipOp = clipOp,
    ) {
        this@drawWithContent.drawContent()
    }
}

private val VELOCITY_THRESHOLD: Dp = 10.dp

@Composable
context(_: Orientation)
internal fun Modifier.sAnchorsDraggable(
    snap: Boolean,
    enabled: Boolean,
    anchors: NonEmptyList<Anchor>,
    getAlong: () -> Along,
    getPosition: () -> Position,
    updateAlong: (Along) -> Unit,
    updatePosition: (Position) -> Unit,
    setIsDragging: (Boolean) -> Unit,
): Modifier {
    if (!enabled) return this

    val velocityThresholdPx =
        with(LocalDensity.current) { VELOCITY_THRESHOLD.toPx() }

    return pointerInput(snap) {
        val totalAlong = size.along.toFloat()
        val velocityThreshold = velocityThresholdPx / totalAlong
        val velocityTracker = VelocityTracker()

        detectDragGestures(
            onDragStart = { offset ->
                setIsDragging(true)
                velocityTracker.resetTracking()
                updateAlong(Along(offset.along / totalAlong))
            },
            onDragCancel = { setIsDragging(false) },
            onDrag = { change, offset ->
                change.consume()
                val newAlong = Along(getAlong().along + offset.along / totalAlong)
                updateAlong(newAlong)
                velocityTracker.addPosition(
                    timeMillis = Clock.System.now().toEpochMilliseconds(),
                    position = Offset(along = newAlong.along, across = 0f),
                )
            },
            onDragEnd = {
                if (snap) {
                    val position = getPosition()
                    val velocity = velocityTracker.calculateVelocity().along
                    val from = position.transform(::floor)
                    val offset = position - from

                    val target = when {
                        velocity > velocityThreshold -> from + 1
                        velocity < -velocityThreshold -> from
                        offset > Position(0.5f) -> from + 1
                        else -> from
                    }
                        .coerceIn(Position(0f), Position(anchors.lastIndex.toFloat()))

                    updatePosition(target)
                }

                setIsDragging(false)
            }
        )
    }
}
