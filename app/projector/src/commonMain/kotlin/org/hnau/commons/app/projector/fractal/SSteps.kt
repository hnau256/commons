package org.hnau.commons.app.projector.fractal

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.lerp
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import arrow.core.NonEmptyList
import arrow.core.toNonEmptyListOrThrow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.hnau.commons.app.projector.fractal.context.LocalFContext
import org.hnau.commons.app.projector.fractal.context.color
import org.hnau.commons.app.projector.fractal.context.containerOverlay
import org.hnau.commons.app.projector.fractal.context.contentOverlay
import org.hnau.commons.app.projector.fractal.distance.LocalDistance
import org.hnau.commons.app.projector.fractal.distance.plus
import org.hnau.commons.app.projector.fractal.size.units
import org.hnau.commons.app.projector.fractal.utils.Importance
import org.hnau.commons.app.projector.fractal.utils.activateIfNeed
import org.hnau.commons.app.projector.uikit.line.ext.IntSize
import org.hnau.commons.app.projector.uikit.line.ext.Offset
import org.hnau.commons.app.projector.uikit.line.ext.across
import org.hnau.commons.app.projector.uikit.line.ext.along
import org.hnau.commons.app.projector.uikit.line.ext.constrainAcross
import org.hnau.commons.app.projector.uikit.line.ext.constrainAlong
import org.hnau.commons.app.projector.uikit.line.ext.copy
import org.hnau.commons.app.projector.uikit.line.ext.offset
import org.hnau.commons.app.projector.uikit.line.ext.placeRelativeA
import org.hnau.commons.app.projector.utils.Orientation
import org.hnau.commons.app.projector.utils.option
import org.hnau.commons.kotlin.Mutable
import org.hnau.commons.kotlin.foldBoolean
import org.hnau.commons.kotlin.ifTrue
import org.hnau.commons.kotlin.mapper.Mapper
import kotlin.math.absoluteValue
import kotlin.math.floor
import kotlin.time.Clock

private data class Anchor(
    val weightBefore: Float,
    val rect: Mutable<Rect> = Mutable(Rect.Zero),
)

@Composable
fun SSteps(
    orientation: Orientation,
    weights: NonEmptyList<Float>,
    position: Float,
    onPositionChanged: ((Float) -> Unit)?,
    modifier: Modifier = Modifier,
    snap: Boolean = true,
    importanceToActivate: Importance? = Importance.default,
    item: @Composable (Int) -> Unit,
) {
    val units = LocalDistance.current.units
    val padding = units.borderWidth
    val cornerRadius = units.cornerRadius
    val containerFContext = LocalFContext
        .current
        .run {
            copy(
                mood = mood.activateIfNeed(
                    importanceToActivate.takeIf { onPositionChanged != null }
                )
            )
        }
        .containerOverlay()
    Box(
        modifier = modifier
            .background(
                color = containerFContext.color,
                shape = RoundedCornerShape(cornerRadius),
            )
            .padding(padding),
        propagateMinConstraints = true,
    ) {
        CompositionLocalProvider(
            value = LocalFContext provides containerFContext
        ) {
            SStepsContent(
                orientation = orientation,
                weights = weights,
                position = position,
                cornerRadius = cornerRadius - padding,
                onPositionChanged = onPositionChanged,
                snap = snap,
                item = item,
            )
        }
    }
}

@Composable
private fun SStepsContent(
    orientation: Orientation,
    weights: NonEmptyList<Float>,
    position: Float,
    cornerRadius: Dp,
    onPositionChanged: ((Float) -> Unit)?,
    snap: Boolean,
    item: @Composable (Int) -> Unit,
) {
    with(orientation) {
        val anchors: NonEmptyList<Anchor> = remember(weights) {
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

        val cornerRadiusPx = with(LocalDensity.current) { cornerRadius.toPx() }
        val backgroundFContent = LocalFContext.current
        val cursorFContext = backgroundFContent.contentOverlay()

        val positionToRect: (Position) -> Rect = remember(anchors) {
            { position ->
                val fromIndex = position.position.toInt()
                val from = anchors[fromIndex.coerceIn(0, anchors.lastIndex)].rect.value
                val to = anchors[(fromIndex + 1).coerceIn(0, anchors.lastIndex)].rect.value
                when {
                    from == to -> from
                    else -> lerp(
                        start = from,
                        stop = to,
                        fraction = position.position - fromIndex
                    )
                }
            }
        }

        val positionAlongMapper: Mapper<Position, Along> = remember(
            positionToRect,
            orientation,
            anchors,
        ) {
            Mapper(
                direct = { position ->
                    position
                        .let(positionToRect)
                        .center
                        .along
                        .let(::Along)
                },
                reverse = { along ->
                    var result: Position?
                    var i = 0
                    do {
                        val from = anchors[i].rect.value.center.along.let(::Along)
                        val to = anchors[i + 1].rect.value.center.along.let(::Along)
                        result = when {
                            along <= from -> i.toFloat().let(::Position)
                            along <= to -> (i + (along - from).along / (to - from).along).let(::Position)
                            else -> null
                        }
                        i++
                    } while (result == null && i < anchors.lastIndex)
                    result ?: anchors.lastIndex.toFloat().let(::Position)
                }
            )
        }

        val localPosition by rememberUpdatedState(position.let(::Position))

        val positionUpdaterScope = rememberCoroutineScope()
        val positionUpdater = remember(
            onPositionChanged,
            positionAlongMapper
        ) {
            onPositionChanged?.let { onPositionChanged ->
                PositionUpdater(
                    scope = positionUpdaterScope,
                    getCurrentPosition = { localPosition },
                    positionAlongMapper = positionAlongMapper,
                    onPositionChanged = { position ->
                        onPositionChanged(position.position)
                    },
                )
            }
        }


        SStepsLayout(
            modifier = Modifier
                .draggable(
                    snap = snap,
                    anchors = anchors,
                    positionUpdater = positionUpdater,
                )
                .drawBehind {
                    val rect = positionToRect(localPosition)
                    drawRoundRect(
                        color = cursorFContext.color,
                        topLeft = rect.topLeft,
                        size = rect.size,
                        cornerRadius = CornerRadius(cornerRadiusPx),
                    )
                },
            anchors = anchors,
            item = { i ->
                listOf(false, true).forEach { selected ->
                    Box(
                        modifier = Modifier
                            .graphicsLayer {
                                val delta =
                                    (i - localPosition.position).absoluteValue.coerceIn(0f, 1f)
                                alpha = selected.foldBoolean(
                                    ifTrue = { 1 - delta },
                                    ifFalse = { delta },
                                )
                            }
                            .option(
                                onPositionChanged
                                    ?.takeIf { !selected }
                                    ?.let { callback ->
                                        Modifier
                                            .clip(RoundedCornerShape(cornerRadius))
                                            .clickable { callback(i.toFloat()) }
                                    }
                            )
                            .padding(
                                LocalDistance.current.units.paddingValues.horizontal.medium,
                            ),
                        propagateMinConstraints = true,
                    ) {
                        CompositionLocalProvider(
                            LocalFContext provides selected.foldBoolean(
                                ifTrue = { cursorFContext },
                                ifFalse = { backgroundFContent }
                            ),
                            LocalDistance provides LocalDistance.current + 1,
                        ) {
                            item(i)
                        }
                    }
                }
            },
        )
    }
}

private val VELOCITY_THRESHOLD: Dp = 10.dp

@Composable
context(_: Orientation)
private fun Modifier.draggable(
    snap: Boolean,
    anchors: NonEmptyList<Anchor>,
    positionUpdater: PositionUpdater?,
): Modifier {
    val positionUpdater = positionUpdater ?: return this

    val velocityThreshold =
        with(LocalDensity.current) { VELOCITY_THRESHOLD.toPx() }

    return pointerInput(snap) {
        detectDragGestures(
            onDragStart = { offset ->
                positionUpdater.setAlong(offset.along.let(::Along))
            },
            onDragCancel = {
                positionUpdater.cancelAnimation()
            },
            onDrag = { change, offset ->
                change.consume()
                positionUpdater.offsetAlong(
                    alongDelta = offset.along.let(::Along),
                )
            },
            onDragEnd = {

                if (!snap) {
                    return@detectDragGestures
                }

                val positon = positionUpdater.getCurrentPosition()
                val velocity = positionUpdater.getVelocity()
                val from = positon.transform(::floor)
                val offset = positon - from

                val target = when {
                    velocity > velocityThreshold -> from + 1
                    velocity < -velocityThreshold -> from
                    offset > Position(0.5f) -> from + 1
                    else -> from
                }
                    .coerceIn(Position(0f), Position(anchors.lastIndex.toFloat()))

                positionUpdater.animateToPosition(
                    target = target,
                )
            }
        )
    }
}

private class PositionUpdater(
    private val scope: CoroutineScope,
    val getCurrentPosition: () -> Position,
    private val positionAlongMapper: Mapper<Position, Along>,
    private val onPositionChanged: (Position) -> Unit,
) {


    private val clock: Clock = Clock.System
    private val velocityTracker = VelocityTracker()

    private var animateJob: Job? = null
        set(value) {
            field?.cancel()
            field = value
        }

    val getCurrentAlong: () -> Along =
        { positionAlongMapper.direct(getCurrentPosition()) }

    fun cancelAnimation() {
        animateJob = null
    }

    context(_: Orientation)
    fun getVelocity(): Float =
        velocityTracker.calculateVelocity().along

    context(_: Orientation)
    fun setAlong(
        along: Along,
    ) {
        setAlongInternal(
            along = along,
        )
    }

    context(_: Orientation)
    fun offsetAlong(
        alongDelta: Along,
    ) {
        setAlong(
            along = getCurrentAlong() + alongDelta
        )
    }

    context(_: Orientation)
    private fun setAlongInternal(
        along: Along,
        resetAnimateJob: Boolean = true,
    ) {
        resetAnimateJob.ifTrue { cancelAnimation() }
        val position = along.let(positionAlongMapper.reverse)
        onPositionChanged(position)
        velocityTracker.addPosition(
            timeMillis = clock.now().toEpochMilliseconds(),
            position = Offset(
                along = along.along,
                across = 0f,
            )
        )
    }

    context(_: Orientation)
    fun animateToPosition(
        target: Position,
    ) {
        animateJob = scope.launch {
            animate(
                initialValue = positionAlongMapper.direct(getCurrentPosition()).along,
                targetValue = positionAlongMapper.direct(target).along,
                initialVelocity = getVelocity(),
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessMedium,
                ),
            ) { along, _ ->
                setAlongInternal(
                    along = along.let(::Along),
                    resetAnimateJob = false,
                )
            }
        }
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
                        },
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

@JvmInline
private value class Along(
    val along: Float,
) : Comparable<Along> {

    override fun compareTo(
        other: Along,
    ): Int = along.compareTo(
        other = other.along,
    )

    inline fun combine(
        other: Along,
        block: (Float, Float) -> Float,
    ): Along = block(
        along,
        other.along,
    ).let(::Along)

    operator fun plus(
        other: Along,
    ): Along = combine(
        other = other,
        block = Float::plus,
    )

    operator fun minus(
        other: Along,
    ): Along = combine(
        other = other,
        block = Float::minus,
    )
}

@JvmInline
private value class Position(
    val position: Float,
) : Comparable<Position> {

    override fun compareTo(
        other: Position,
    ): Int = position.compareTo(
        other = other.position,
    )

    inline fun transform(
        block: (Float) -> Float,
    ): Position = block(position).let(::Position)

    inline fun combine(
        other: Position,
        block: (Float, Float) -> Float,
    ): Position = block(
        position,
        other.position,
    ).let(::Position)

    operator fun plus(
        other: Position,
    ): Position = combine(
        other = other,
        block = Float::plus,
    )

    operator fun minus(
        other: Position,
    ): Position = combine(
        other = other,
        block = Float::minus,
    )

    operator fun plus(
        other: Number,
    ): Position = transform { position ->
        position + other.toFloat()
    }

    operator fun minus(
        other: Number,
    ): Position = transform { position ->
        position - other.toFloat()
    }
}