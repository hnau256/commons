package org.hnau.commons.app.projector.fractal.anchor

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import org.hnau.commons.app.projector.uikit.line.ext.Size
import org.hnau.commons.app.projector.uikit.line.ext.across
import org.hnau.commons.app.projector.uikit.line.ext.along
import org.hnau.commons.app.projector.utils.Orientation
import org.hnau.commons.app.projector.utils.option
import org.hnau.commons.kotlin.foldBoolean
import org.hnau.commons.kotlin.foldNullable
import org.hnau.commons.kotlin.ifTrue
import org.hnau.commons.kotlin.mapper.Mapper
import org.hnau.commons.kotlin.mapper.plus

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
            val active = isEnabled || drawProgress
            copy(
                mood = importanceToActivate
                    .takeIf { active }
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
                cornerRadius = (cornerRadius - padding).coerceAtLeast(0.dp),
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

        val anchors = remember(weights) {

            NonEmptyList(
                head = Anchor(weightBefore = 0f),
                tail = weights
                    .any { weight -> weight > 0f }
                    .foldBoolean(
                        ifTrue = { weights },
                        ifFalse = { weights.map { 1f } }
                    )
                    .map(::Anchor),
            )
        }

        val positionAlongMapper = remember(anchors) {
            val cumulativeWeights = anchors.runningFold(
                initial = 0f,
            ) { acc, anchor -> acc + anchor.weightBefore }

            val totalWeight = cumulativeWeights.last()

            val anchorFractions = cumulativeWeights
                .drop(1)
                .map { it / totalWeight }

            Mapper(Position::position, ::Position) +
                    anchorFractions.knotsMapper() +
                    Mapper(::Along, Along::along)
        }

        val along = rememberSAnchorsCursorAlong(
            state = state,
            mapper = positionAlongMapper,
        )

        val drawCursor = isEnabled || !drawProgress

        val cornerRadiusPx = with(LocalDensity.current) { cornerRadius.toPx() }
        val backgroundFContext = LocalFContext.current
        val progressFContext = drawProgress.ifTrue {
            isEnabled.foldBoolean(
                ifTrue = { backgroundFContext.containerOverlay() },
                ifFalse = { backgroundFContext.contentOverlay() },
            )
        }
        val cursorFContext = backgroundFContext.contentOverlay()

        val selectionStates = remember(drawCursor) {
            listOfNotNull(
                false,
                drawCursor.ifTrue { true },
            )
        }

        val anchorRects = remember(anchors) {
            MutableList(anchors.size) { Rect.Zero }
        }

        val rectsMapper = remember(anchorRects) {
            anchorRects.knotsMapper(
                lerp = ::lerp,
                knot = { rect -> rect.center.along },
            )
        }

        val getCursorRect = {
            val position = positionAlongMapper.reverse(along.value)
            rectsMapper.direct(position.position)
        }

        val positionAtPx = { along: Float ->
            anchorRects
                .map { rect -> rect.center.along }
                .knotsMapper()
                .reverse(along)
                .let(::Position)

            sAnchorsCursorPosition(anchorRects, along)
        }

        SAnchorsLayout(
            modifier = Modifier
                .sAnchorsDraggable(
                    snap = snap,
                    enabled = isEnabled,
                    anchors = anchors,
                    getCursorRect = getCursorRect,
                    getPosition = { state.position },
                    positionAtPx = positionAtPx,
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
                                ifFalse = { backgroundFContext },
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

context(_: Orientation)
private fun sAnchorsCursorPosition(
    rects: List<Rect>,
    alongPx: Float,
): Position = rects
    .map { rect -> rect.center.along }
    .knotsMapper()
    .reverse(alongPx)
    .let(::Position)
