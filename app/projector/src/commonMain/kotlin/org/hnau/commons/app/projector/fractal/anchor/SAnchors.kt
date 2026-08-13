package org.hnau.commons.app.projector.fractal.anchor

import androidx.compose.animation.core.animate
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.lerp
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import arrow.core.NonEmptyList
import kotlinx.coroutines.flow.collectLatest
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
import org.hnau.commons.app.projector.uikit.line.ext.Size
import org.hnau.commons.app.projector.uikit.line.ext.across
import org.hnau.commons.app.projector.uikit.line.ext.along
import org.hnau.commons.app.projector.utils.Orientation
import org.hnau.commons.app.projector.utils.option
import org.hnau.commons.kotlin.foldBoolean
import org.hnau.commons.kotlin.foldNullable
import org.hnau.commons.kotlin.ifTrue

@Composable
fun SAnchors(
    orientation: Orientation,
    weights: NonEmptyList<Float>,
    state: SAnchorsState,
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true,
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
                    .takeIf { isEnabled }
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
                isEnabled = isEnabled,
                orientation = orientation,
                weights = weights,
                cornerRadius = cornerRadius - padding,
                snap = snap,
                drawProgress = drawProgress,
                item = item,
            )
        }
    }
}

@Composable
private fun SAnchorsContent(
    state: SAnchorsState,
    isEnabled: Boolean,
    orientation: Orientation,
    weights: NonEmptyList<Float>,
    cornerRadius: Dp,
    snap: Boolean,
    drawProgress: Boolean,
    item: (@Composable (Int) -> Unit)?,
) {
    with(orientation) {

        val anchors = remember(weights) { buildAnchors(weights) }
        val mapper = remember(anchors) { SAnchorsMapper(anchors) }
        val along = remember(mapper, state) { mutableStateOf(mapper.direct(state.position)) }

        LaunchedEffect(mapper) {
            snapshotFlow { state.position to state.isDragging }
                .collectLatest { (position, dragging) ->
                    val target = mapper.direct(position)
                    val current = along.value
                    if (target == current) return@collectLatest
                    dragging.foldBoolean(
                        ifTrue = {
                            along.value = target
                        },
                        ifFalse = {
                            animate(
                                initialValue = current,
                                targetValue = target,
                                typeConverter = Along.twoWayConverter,
                                animationSpec = spring(),
                            ) { value, _ ->
                                along.value = value
                            }
                        },
                    )
                }
        }

        val drawCursor = isEnabled || !drawProgress

        val cornerRadiusPx = with(LocalDensity.current) { cornerRadius.toPx() }
        val backgroundFContent = LocalFContext.current
        val progressFContext = drawProgress.ifTrue {
            isEnabled.foldBoolean(
                ifTrue = { backgroundFContent.containerOverlay() },
                ifFalse = { backgroundFContent.contentOverlay() },
            )
        }
        val cursorFContext = backgroundFContent.contentOverlay()

        val selectionStates = remember(drawCursor) {
            listOfNotNull(
                false,
                drawCursor.ifTrue { true },
            )
        }

        val anchorRects = remember(anchors) {
            MutableList(anchors.size) { Rect.Zero }
        }

        val getCursorRect = { sAnchorsCursorRect(anchorRects, mapper.reverse(along.value)) }

        SAnchorsLayout(
            modifier = Modifier
                .sAnchorsDraggable(
                    snap = snap,
                    enabled = isEnabled,
                    anchors = anchors,
                    getAlong = { mapper.direct(state.position) },
                    getPosition = { state.position },
                    updateAlong = { newAlong -> state.position = mapper.reverse(newAlong) },
                    updatePosition = { position -> state.position = position },
                    setIsDragging = { value -> state.isDragging = value },
                )
                .drawBehind {

                    val cornerRadius = CornerRadius(cornerRadiusPx)
                    val cursorRect = getCursorRect()

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
                                    along = isEnabled.foldBoolean(
                                        ifTrue = { cursorRect.topLeft.along + cursorRect.size.along },
                                        ifFalse = { size.along * along.value.along },
                                    ),
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
                            drawRoundRect(
                                color = cursorFContext.color,
                                topLeft = cursorRect.topLeft,
                                size = cursorRect.size,
                                cornerRadius = cornerRadius,
                            )
                        }
                    }
                },
            anchors = anchors,
            rects = anchorRects,
            item = { i ->
                Box(
                    modifier = Modifier.option(
                        (isEnabled && item != null).ifTrue {
                            Modifier
                                .clip(RoundedCornerShape(cornerRadius))
                                .clickable { state.position = Position(i.toFloat()) }
                        },
                    ),
                    propagateMinConstraints = true,
                ) {

                    selectionStates.forEach { selected ->
                        Box(
                            modifier = Modifier.option(
                                drawCursor.ifTrue {
                                    Modifier.sAnchorsClipToCursorRect(
                                        getAnchorRect = { anchorRects[i] },
                                        getCursorRect = getCursorRect,
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
                                                .run {
                                                    isEnabled.foldBoolean(
                                                        ifTrue = { iconSize + padding.across.extraSmall * 2 },
                                                        ifFalse = { padding.across.extraSmall * 2 },
                                                    )
                                                }
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

private fun sAnchorsCursorRect(
    rects: List<Rect>,
    position: Position,
): Rect {
    val i = position.position.toInt().coerceIn(0, rects.lastIndex)
    val from = rects[i]
    val to = rects[(i + 1).coerceIn(0, rects.lastIndex)]
    return if (from == to) from else lerp(from, to, position.position - i)
}
