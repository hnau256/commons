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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import arrow.core.NonEmptyList
import org.hnau.commons.app.projector.fractal.anchor.Along
import org.hnau.commons.app.projector.fractal.anchor.Anchor
import org.hnau.commons.app.projector.fractal.anchor.Position
import org.hnau.commons.app.projector.uikit.line.ext.along
import org.hnau.commons.app.projector.utils.Orientation
import kotlin.math.floor


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
    anchors: NonEmptyList<Anchor>,
    setIsDragging: (Boolean) -> Unit,
    getAlong: () -> Along,
    getPosition: () -> Position,
    getVelocity: () -> Along,
    updateAlong: ((Along) -> Unit)?,
    updatePosition: ((Position) -> Unit)?,
): Modifier {
    val updateAlong = updateAlong ?: return this
    val updatePosition = updatePosition ?: return this

    val velocityThreshold =
        with(LocalDensity.current) { VELOCITY_THRESHOLD.toPx() }

    return pointerInput(snap) {

        detectDragGestures(
            onDragStart = { offset ->
                setIsDragging(true)
                val along = offset.along.let(::Along)
                updateAlong(along)
            },
            onDragCancel = { setIsDragging(false) },
            onDrag = { change, offset ->
                change.consume()
                val newAlong = getAlong() + offset.along.let(::Along)
                updateAlong(newAlong)
            },
            onDragEnd = {
                if (snap) {
                    val position = getPosition()
                    val velocity = getVelocity().along
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