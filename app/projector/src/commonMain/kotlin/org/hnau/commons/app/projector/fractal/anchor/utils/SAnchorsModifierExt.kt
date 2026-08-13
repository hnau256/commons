package org.hnau.commons.app.projector.fractal.anchor.utils

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
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
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.hnau.commons.app.projector.fractal.anchor.Position
import org.hnau.commons.app.projector.fractal.anchor.RectCenterAlongPx
import org.hnau.commons.app.projector.uikit.line.ext.along
import org.hnau.commons.app.projector.utils.Orientation
import org.hnau.commons.app.projector.utils.fold
import org.hnau.commons.kotlin.Mutable
import org.hnau.commons.kotlin.ifFalse
import kotlin.math.floor


context(orientation: Orientation)
internal fun Modifier.sAnchorsClipToCursorRect(
    getAnchorRect: () -> Rect,
    getCursorRect: () -> Rect,
    cornerRadiusPx: Float,
    clipOp: ClipOp,
): Modifier = drawWithContent {

    val anchorRect = getAnchorRect()
    val cursorRect = getCursorRect()

    val alongSize = size.along
    val clipStart = (cursorRect.topLeft.along - anchorRect.topLeft.along)
        .coerceIn(0f, alongSize)
    val clipEnd = (cursorRect.topLeft.along + cursorRect.size.along - anchorRect.topLeft.along)
        .coerceIn(clipStart, alongSize)

    val path = Path().apply {
        addRoundRect(
            orientation.fold(
                ifHorizontal = {
                    RoundRect(
                        left = clipStart,
                        top = 0f,
                        right = clipEnd,
                        bottom = size.height,
                        radiusX = cornerRadiusPx,
                        radiusY = cornerRadiusPx,
                    )
                },
                ifVertical = {
                    RoundRect(
                        left = 0f,
                        top = clipStart,
                        right = size.width,
                        bottom = clipEnd,
                        radiusX = cornerRadiusPx,
                        radiusY = cornerRadiusPx,
                    )
                },
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

/**
 * Минимальная скорость (в dp/с), при которой drag по завершении
 * перекидывает курсор к соседнему якорю независимо от позиции.
 */
private val FLING_VELOCITY_THRESHOLD: Dp = 100.dp

@Composable
context(_: Orientation)
internal fun Modifier.sAnchorsDraggable(
    snap: Boolean,
    enabled: Boolean,
    maxPosition: Position,
    getCursorRect: () -> Rect,
    getPosition: () -> Position,
    positionAtPx: (RectCenterAlongPx) -> Position,
    updatePosition: (Position) -> Unit,
    setIsDragging: (Boolean) -> Unit,
): Modifier {
    if (!enabled) return this

    val flingVelocityThresholdPxPerSecond =
        with(LocalDensity.current) { FLING_VELOCITY_THRESHOLD.toPx() }

    return pointerInput(snap, maxPosition) {
        val velocityTracker = VelocityTracker()
        var grabOffsetPx = 0f

        coroutineScope {
            snap.ifFalse {
                launch {
                    detectTapGestures { offset ->
                        updatePosition(positionAtPx(offset.along.let(::RectCenterAlongPx)))
                    }
                }
            }

            detectDragGestures(
                onDragStart = { offset ->
                    setIsDragging(true)
                    velocityTracker.resetTracking()
                    grabOffsetPx = offset.along - getCursorRect().center.along
                },
                onDragCancel = { setIsDragging(false) },
                onDrag = { change, _ ->
                    change.consume()
                    updatePosition(positionAtPx((change.position.along - grabOffsetPx).let(::RectCenterAlongPx)))
                    velocityTracker.addPosition(
                        timeMillis = change.uptimeMillis,
                        position = change.position,
                    )
                },
                onDragEnd = {
                    if (snap) {
                        val position = getPosition()
                        val velocityPxPerSecond = velocityTracker.calculateVelocity().along
                        val from = position.transform(::floor)
                        val offset = position - from

                        val target = when {
                            velocityPxPerSecond > flingVelocityThresholdPxPerSecond -> from + 1
                            velocityPxPerSecond < -flingVelocityThresholdPxPerSecond -> from
                            offset > Position(0.5f) -> from + 1
                            else -> from
                        }
                            .coerceIn(Position(0f), maxPosition)

                        updatePosition(target)
                    }

                    setIsDragging(false)
                }
            )
        }
    }
}
