package org.hnau.commons.app.projector.fractal

import androidx.compose.animation.core.animate
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.onDrag
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.lerp
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import arrow.core.NonEmptyList
import arrow.core.toNonEmptyListOrThrow
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.hnau.commons.app.projector.fractal.context.LocalFContext
import org.hnau.commons.app.projector.fractal.context.color
import org.hnau.commons.app.projector.fractal.context.contentOverlay
import org.hnau.commons.app.projector.fractal.distance.LocalDistance
import org.hnau.commons.app.projector.fractal.size.units
import org.hnau.commons.app.projector.uikit.line.ext.IntSize
import org.hnau.commons.app.projector.uikit.line.ext.across
import org.hnau.commons.app.projector.uikit.line.ext.along
import org.hnau.commons.app.projector.uikit.line.ext.constrainAcross
import org.hnau.commons.app.projector.uikit.line.ext.constrainAlong
import org.hnau.commons.app.projector.uikit.line.ext.copy
import org.hnau.commons.app.projector.uikit.line.ext.offset
import org.hnau.commons.app.projector.uikit.line.ext.placeRelativeA
import org.hnau.commons.app.projector.utils.Orientation
import org.hnau.commons.kotlin.Mutable
import org.hnau.commons.kotlin.foldBoolean
import kotlin.math.absoluteValue
import kotlin.time.Clock

private data class Anchor(
    val weightBefore: Float,
    val rect: Mutable<Rect> = Mutable(Rect.Zero),
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SSteps(
    orientation: Orientation,
    weights: NonEmptyList<Float>,
    position: Float,
    onPositionChanged: (Float) -> Unit,
    modifier: Modifier = Modifier,
    snap: Boolean = true,
    item: @Composable (Int) -> Unit,
) {
    with(orientation) {
        val anchors = remember(weights) {
            buildList {
                add(
                    Anchor(
                        weightBefore = 0f,
                    )
                )
                val atLeastOneWeight = weights.any { weight -> weight > 0 }
                addAll(
                    weights.map { weight ->
                        Anchor(
                            atLeastOneWeight.foldBoolean(
                                ifTrue = { weight },
                                ifFalse = { 1f }
                            )
                        )
                    }
                )
            }.toNonEmptyListOrThrow()
        }

        val distance = LocalDistance.current
        val cornerRadius = with(LocalDensity.current) { distance.units.cornerRadius.toPx() }
        val backgroundFContent = LocalFContext.current
        val cursorFContext = backgroundFContent.contentOverlay()

        val positionToRect: (Float) -> Rect = remember(anchors) {
            { position ->
                val fromIndex = position.toInt()
                val from = anchors[fromIndex.coerceIn(0, anchors.lastIndex)].rect.value
                val to = anchors[(fromIndex + 1).coerceIn(0, anchors.lastIndex)].rect.value
                when {
                    from == to -> from
                    else -> lerp(
                        start = from,
                        stop = to,
                        fraction = position - fromIndex
                    )
                }
            }
        }

        val positionToAlong: (Float) -> Float = remember(positionToRect, orientation) {
            { position -> positionToRect(position).center.along }
        }

        val alongToPosition: (Float) -> Float = remember(anchors, orientation) {
            { along ->
                var result: Float?
                var i = 0
                do {
                    val from = anchors[i].rect.value.center.along
                    val to = anchors[i + 1].rect.value.center.along
                    result = when {
                        along <= from -> i.toFloat()
                        along <= to -> i + (along - from) / (to - from)
                        else -> null
                    }
                    i++
                } while (result == null && i < anchors.lastIndex)
                result ?: anchors.lastIndex.toFloat()
            }
        }

        val velocityTracker = remember { VelocityTracker() }

        val scope = rememberCoroutineScope()
        var snapJob by remember { mutableStateOf<Job?>(null) }

        SStepsLayout(
            modifier = modifier
                .onDrag(
                    onDragStart = { offset ->
                        snapJob?.cancel()
                        velocityTracker.resetTracking()
                        velocityTracker.addPosition(
                            Clock.System.now().toEpochMilliseconds(),
                            offset
                        )
                    },
                    onDrag = { offset ->
                        velocityTracker.addPosition(
                            Clock.System.now().toEpochMilliseconds(),
                            offset
                        )
                        val newAlong = positionToAlong(position) + offset.along
                        onPositionChanged(alongToPosition(newAlong))
                    },
                    onDragEnd = {
                        if (!snap) {
                            return@onDrag
                        }
                        val currentAlong = positionToAlong(position)
                        val velocity = velocityTracker.calculateVelocity().along
                        val from = position.toInt()
                        val offset = position - from
                        val targetAlong = when {
                            velocity > 400 -> from + 1
                            velocity < -400 -> from
                            offset > 0.5 -> from + 1
                            else -> from
                        }.coerceIn(0, anchors.lastIndex).toFloat().let(positionToAlong)
                        snapJob = scope.launch {
                            animate(
                                initialValue = currentAlong,
                                targetValue = targetAlong,
                                initialVelocity = velocity,
                                animationSpec = spring(),
                            ) { along, _ ->
                                onPositionChanged(alongToPosition(along))
                            }
                        }
                    }
                )
                .drawBehind {
                    val rect = positionToRect(position)
                    drawRoundRect(
                        color = cursorFContext.color,
                        topLeft = rect.topLeft,
                        size = rect.size,
                        cornerRadius = CornerRadius(cornerRadius),
                    )
                },
            anchors = anchors,
            item = { i ->
                listOf(false, true).forEach { selected ->
                    Box(
                        modifier = Modifier.graphicsLayer {
                            val delta = (i - position).absoluteValue.coerceIn(0f, 1f)
                            alpha = selected.foldBoolean(
                                ifTrue = { 1 - delta },
                                ifFalse = { delta },
                            )
                        },
                        propagateMinConstraints = true,
                    ) {
                        CompositionLocalProvider(
                            value = LocalFContext provides selected.foldBoolean(
                                ifTrue = { cursorFContext },
                                ifFalse = { backgroundFContent }
                            )
                        ) {
                            item(i)
                        }
                    }
                }
            },
        )
    }
}

@Composable
context(_: Orientation)
private fun SStepsLayout(
    anchors: NonEmptyList<Anchor>,
    item: @Composable (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Layout(
        modifier = modifier,
        content = {
            anchors.forEachIndexed { index, anchor ->
                Box(
                    modifier = Modifier
                        .onGloballyPositioned { coordinates ->
                            anchor.rect.value = coordinates.boundsInParent()
                        }
                        .padding(
                            LocalDistance.current.units.paddingValues.horizontal.medium,
                        ),
                    propagateMinConstraints = true,
                ) { item(index) }
            }
        }
    ) { measurables, constraints ->

        val (placeables, _, totalWeight) = measurables
            .foldIndexed(
                initial = Triple(
                    emptyList<Placeable>(),
                    0,
                    0f
                ),
            ) { i, (placeables, usedAlong, totalWeight), measurable ->
                val childConstraints = constraints.offset(
                    along = -usedAlong,
                ).copy(
                    minAlong = 0,
                )
                val placeable = measurable.measure(
                    constraints = childConstraints,
                )
                Triple(
                    placeables + placeable,
                    usedAlong + placeable.along,
                    totalWeight + anchors[i].weightBefore,
                )
            }

        val childrenAlong = placeables.sumOf { placeable -> placeable.along }

        val along = constraints.constrainAlong(
            along = childrenAlong
        )

        val across = constraints.constrainAcross(
            across = placeables.maxOf { placeable -> placeable.across },
        )

        val additionalAlongByWeight = (along - childrenAlong) / totalWeight

        val size = IntSize(
            along = along,
            across = across,
        )

        var alongOffset = 0f
        layout(
            width = size.width,
            height = size.height,
        ) {
            placeables.forEachIndexed { i, placeable ->
                val anchor = anchors[i].weightBefore
                val isLast = i == anchors.lastIndex
                val alongPosition = isLast.foldBoolean(
                    ifTrue = { along - placeable.along },
                    ifFalse = {
                        val result = alongOffset + anchor * additionalAlongByWeight
                        alongOffset = result + placeable.along
                        result.toInt()
                    }
                )
                placeable.placeRelativeA(
                    along = alongPosition,
                    across = (across - placeable.across) / 2,
                )
            }
        }
    }
}