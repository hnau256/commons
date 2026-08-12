package org.hnau.commons.app.projector.fractal.anchor

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import arrow.core.NonEmptyList
import org.hnau.commons.app.projector.fractal.anchor.utils.SAnchorsLayout
import org.hnau.commons.app.projector.fractal.anchor.utils.sAnchorsClipToCursorRect
import org.hnau.commons.app.projector.fractal.anchor.utils.sAnchorsDraggable
import org.hnau.commons.app.projector.fractal.context.LocalFContext
import org.hnau.commons.app.projector.fractal.context.color
import org.hnau.commons.app.projector.fractal.context.containerOverlay
import org.hnau.commons.app.projector.fractal.context.contentOverlay
import org.hnau.commons.app.projector.fractal.distance.LocalDistance
import org.hnau.commons.app.projector.fractal.distance.plus
import org.hnau.commons.app.projector.fractal.padding.LocalContentPadding
import org.hnau.commons.app.projector.fractal.padding.LocalContentPaddingBox
import org.hnau.commons.app.projector.fractal.size.units
import org.hnau.commons.app.projector.fractal.utils.Importance
import org.hnau.commons.app.projector.fractal.utils.activate
import org.hnau.commons.app.projector.uikit.line.ext.IntSize
import org.hnau.commons.app.projector.uikit.line.ext.Offset
import org.hnau.commons.app.projector.uikit.line.ext.Size
import org.hnau.commons.app.projector.uikit.line.ext.across
import org.hnau.commons.app.projector.uikit.line.ext.along
import org.hnau.commons.app.projector.uikit.line.ext.constrainAcross
import org.hnau.commons.app.projector.uikit.line.ext.constrainAlong
import org.hnau.commons.app.projector.uikit.line.ext.copy
import org.hnau.commons.app.projector.uikit.line.ext.offset
import org.hnau.commons.app.projector.uikit.line.ext.placeRelative
import org.hnau.commons.app.projector.utils.Orientation
import org.hnau.commons.app.projector.utils.option
import org.hnau.commons.kotlin.foldBoolean
import org.hnau.commons.kotlin.foldNullable
import org.hnau.commons.kotlin.ifTrue
import kotlin.math.floor

@Composable
fun SAnchors(
    orientation: Orientation,
    state: SAnchorsState,
    mediator: SAnchorsStateMediator?,
    modifier: Modifier = Modifier,
    snap: Boolean = true,
    drawProgress: Boolean = false,
    importanceToActivate: Importance? = Importance.default,
    item: (@Composable (Int) -> Unit)?,
) {
    val units = LocalDistance.current.units
    val padding = units.borderWidth
    val cornerRadius = units.cornerRadius

    val containerFContext = LocalFContext
        .current
        .run {
            copy(
                mood = importanceToActivate
                    .takeIf { mediator != null }
                    .foldNullable(
                        ifNull = { mood },
                        ifNotNull = mood::activate,
                    )
            )
        }
        .containerOverlay()

    LocalContentPaddingBox(
        modifier = modifier
            .background(
                color = containerFContext.color,
                shape = RoundedCornerShape(cornerRadius),
            )
            .padding(padding),
    ) {
        CompositionLocalProvider(
            value = LocalFContext provides containerFContext
        ) {
            SAnchorsContent(
                state = state,
                mediator = mediator,
                orientation = orientation,
                cornerRadius = cornerRadius - padding,
                snap = snap,
                drawProgress = drawProgress,
                item = item,
            )
        }
    }
}

data class Anchor(
    val weightBefore: Float,
    var rect: Rect = Rect.Zero,
)

@Composable
private fun SAnchorsContent(
    state: SAnchorsState,
    mediator: SAnchorsStateMediator?,
    orientation: Orientation,
    cornerRadius: Dp,
    snap: Boolean,
    drawProgress: Boolean,
    item: (@Composable (Int) -> Unit)?,
) {
    with(orientation) {

        val drawCursor = mediator != null || !drawProgress

        val cornerRadiusPx = with(LocalDensity.current) { cornerRadius.toPx() }
        val backgroundFContent = LocalFContext.current
        val progressFContext = drawProgress.ifTrue {
            if (mediator != null) backgroundFContent.containerOverlay()
            else backgroundFContent.contentOverlay()
        }
        val cursorFContext = backgroundFContent.contentOverlay()

        val selectionStates = remember(drawCursor) {
            listOfNotNull(
                false,
                drawCursor.ifTrue { true },
            )
        }

        SAnchorsLayout(
            modifier = Modifier
                .sAnchorsDraggable(
                    snap = snap,
                    anchors = state.anchors,
                    getAlong = state::along,
                    getPosition = state::position,
                    updateAlong = mediator?.let { it::updateAlong },
                    updatePosition = mediator?.let { it::updatePosition },
                    getVelocity = state::velocity,
                    setIsDragging = { value -> mediator?.setIsDragging(value) },
                )
                .drawBehind {

                    val cornerRadius = CornerRadius(cornerRadiusPx)

                    clipPath(
                        Path().apply {
                            addRoundRect(
                                RoundRect(
                                    rect = Rect(
                                        offset = Offset.Zero,
                                        size = size,
                                    ),
                                    radiusX = cornerRadiusPx,
                                    radiusY = cornerRadiusPx,
                                ),
                            )
                        }
                    ) {

                        progressFContext?.let { fContext ->
                            val progressRect = Rect(
                                offset = Offset.Zero,
                                size = Size(
                                    along = if (mediator != null) state.along.along
                                    else (size.along * (state.position.position / state.anchors.lastIndex.toFloat())),
                                    across = size.across,
                                ),
                            )
                            drawRoundRect(
                                color = fContext.color,
                                topLeft = progressRect.topLeft,
                                size = progressRect.size,
                                cornerRadius = cornerRadius,
                            )
                        }

                        drawCursor.ifTrue {
                            val cursorRect = state.cursorRect
                            drawRoundRect(
                                color = cursorFContext.color,
                                topLeft = cursorRect.topLeft,
                                size = cursorRect.size,
                                cornerRadius = cornerRadius,
                            )
                        }
                    }
                },
            anchors = state.anchors,
            item = { i ->
                Box(
                    modifier = Modifier
                        .option(
                            mediator
                                ?.let { it::updatePosition }
                                ?.takeIf { item != null }
                                ?.let { callback ->
                                    Modifier
                                        .clip(RoundedCornerShape(cornerRadius))
                                        .clickable { callback(Position(i.toFloat())) }
                                }
                        ),
                    propagateMinConstraints = true,
                ) {

                    selectionStates.forEach { selected ->
                        Box(
                            modifier = Modifier.option(
                                drawCursor.ifTrue {
                                    Modifier.sAnchorsClipToCursorRect(
                                        getAnchorRect = { state.anchors[i].rect },
                                        getCursorRect = { state.cursorRect },
                                        cornerRadiusPx = cornerRadiusPx,
                                        clipOp = selected.foldBoolean(
                                            ifTrue = { ClipOp.Intersect },
                                            ifFalse = { ClipOp.Difference },
                                        ),
                                    )
                                }
                            ),
                            propagateMinConstraints = true,
                        ) {
                            val itemContext = selected.foldBoolean(
                                ifTrue = { cursorFContext },
                                ifFalse = { backgroundFContent },
                            )

                            item.foldNullable(
                                ifNull = {
                                    Box(
                                        modifier = Modifier.size(
                                            LocalDistance
                                                .current
                                                .units
                                                .run { iconSize + padding.across.extraSmall * 2 }
                                        )
                                    )
                                },
                                ifNotNull = { item ->
                                    CompositionLocalProvider(
                                        LocalContentPadding provides LocalDistance.current.units.paddingValues.horizontal.medium,
                                        LocalFContext provides itemContext,
                                        LocalDistance provides LocalDistance.current + 1,
                                    ) {
                                        item(i)
                                    }
                                }
                            )
                        }
                    }
                }
            },
        )
    }
}