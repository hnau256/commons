package org.hnau.commons.app.projector.fractal

import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.TwoWayConverter
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.lerp
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import arrow.core.NonEmptyList
import arrow.core.toNonEmptyListOrThrow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
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
import org.hnau.commons.app.projector.utils.observe
import org.hnau.commons.app.projector.utils.option
import org.hnau.commons.kotlin.Mutable
import org.hnau.commons.kotlin.coroutines.createChild
import org.hnau.commons.kotlin.foldBoolean
import org.hnau.commons.kotlin.foldNullable
import org.hnau.commons.kotlin.ifTrue
import org.hnau.commons.kotlin.mapper.Mapper
import kotlin.math.floor
import kotlin.time.Clock

@Composable
fun SAnchors(
    orientation: Orientation,
    weights: NonEmptyList<Float>,
    getPosition: () -> Float,
    onPositionChanged: ((Float) -> Unit)?,
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
                    .takeIf { onPositionChanged != null }
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
                orientation = orientation,
                weights = weights,
                getPosition = getPosition,
                cornerRadius = cornerRadius - padding,
                onPositionChanged = onPositionChanged,
                snap = snap,
                drawProgress = drawProgress,
                item = item,
            )
        }
    }
}

private class PositionHolder(
    private val orientation: Orientation,
    scope: CoroutineScope,
    getPosition: () -> Position,
    val setPosition: ((Position) -> Unit)?,
    private val getAlongVisibilityThreshold: () -> Along,
    private val anchors: NonEmptyList<Anchor>,
) {

    private fun getRect(
        position: Position,
    ): Rect {
        val fromIndex = position.position.toInt()
        val from = anchors[fromIndex.coerceIn(0, anchors.lastIndex)].rect
        val to = anchors[(fromIndex + 1).coerceIn(0, anchors.lastIndex)].rect
        return when {
            from == to -> from
            else -> lerp(
                start = from,
                stop = to,
                fraction = position.position - fromIndex
            )
        }
    }

    val positionAlongMapper: Mapper<Position, Along> = Mapper(
        direct = { position ->
            with(orientation) {
                position
                    .let(::getRect)
                    .center
                    .along
                    .let(::Along)
            }
        },
        reverse = { along ->
            with(orientation) {
                var result: Position?
                var i = 0
                do {
                    val from = anchors[i].rect.center.along.let(::Along)
                    val to = anchors[i + 1].rect.center.along.let(::Along)
                    result = when {
                        along <= from -> i.toFloat().let(::Position)
                        along <= to -> (i + (along - from).along / (to - from).along).let(::Position)
                        else -> null
                    }
                    i++
                } while (result == null && i < anchors.lastIndex)
                result ?: anchors.lastIndex.toFloat().let(::Position)
            }
        }
    )

    var isDragging: Boolean by mutableStateOf(false)

    private val alongRaw: Along
            by derivedStateOf { getPosition().let(positionAlongMapper.direct) }

    var along: Along by mutableStateOf(alongRaw)
        private set

    val position: Position by derivedStateOf {
        along.let(positionAlongMapper.reverse)
    }

    val fraction: Float by derivedStateOf {
        position.position / anchors.lastIndex.toFloat()
    }

    val cursorRect: Rect
        get() = getRect(position)

    val setAlong: ((Along) -> Unit)? = setPosition?.let { set ->
        { along: Along -> set(along.let(positionAlongMapper.reverse)) }
    }

    private val velocityTracker = VelocityTracker()

    val velocity: Along
        get() = with(orientation) {
            velocityTracker.calculateVelocity().along.let(::Along)
        }

    init {
        scope.launch {
            derivedStateOf { isDragging.ifTrue { along } }.observe { alongOrNull ->
                alongOrNull.foldNullable(
                    ifNull = { velocityTracker.resetTracking() },
                    ifNotNull = { along ->
                        velocityTracker.addPosition(
                            timeMillis = Clock.System.now().toEpochMilliseconds(),
                            position = with(orientation) {
                                Offset(
                                    along = along.along,
                                    across = 0f,
                                )
                            }
                        )
                    }
                )
            }
        }
        scope.launch {
            val triggerState = derivedStateOf { getPosition() to isDragging }
            triggerState.observe { (position, dragging) ->

                val currentAlong = along

                val targetAlong = positionAlongMapper
                    .direct(position)
                    .takeIf { it != currentAlong }
                    ?: return@observe

                if (dragging) {
                    along = targetAlong
                    return@observe
                }

                animate(
                    initialValue = currentAlong,
                    targetValue = targetAlong,
                    typeConverter = Along.twoWayConverter,
                    animationSpec = spring(
                        visibilityThreshold = getAlongVisibilityThreshold(),
                    ),
                ) { value, _ ->
                    along = value
                }
            }
        }
    }
}

private data class Anchor(
    val weightBefore: Float,
    var rect: Rect = Rect.Zero,
)

@Composable
private fun SAnchorsContent(
    orientation: Orientation,
    weights: NonEmptyList<Float>,
    getPosition: () -> Float,
    cornerRadius: Dp,
    onPositionChanged: ((Float) -> Unit)?,
    snap: Boolean,
    drawProgress: Boolean,
    item: (@Composable (Int) -> Unit)?,
) {
    with(orientation) {

        val drawCursor = !(onPositionChanged == null && drawProgress)

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


        val alongVisibilityThreshold by rememberUpdatedState(Along.VisibilityThreshold)

        val coroutineScope = rememberCoroutineScope { Dispatchers.Unconfined }
        val positionHolderCoroutineScope = remember { Mutable<CoroutineScope?>(null) }

        val positionHolder = remember(anchors) {
            positionHolderCoroutineScope.value?.cancel()
            val scope = coroutineScope.createChild()
            positionHolderCoroutineScope.value = scope
            PositionHolder(
                orientation = orientation,
                scope = scope,
                getPosition = { getPosition().let(::Position) },
                setPosition = onPositionChanged?.let { set ->
                    { position -> position.position.let(set) }
                },
                getAlongVisibilityThreshold = { alongVisibilityThreshold },
                anchors = anchors,
            )
        }

        val cornerRadiusPx = with(LocalDensity.current) { cornerRadius.toPx() }
        val backgroundFContent = LocalFContext.current
        val progressFContext = drawProgress.ifTrue {
            onPositionChanged.foldNullable(
                ifNull = { backgroundFContent.contentOverlay() },
                ifNotNull = { backgroundFContent.containerOverlay() }
            )
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
                .draggable(
                    snap = snap,
                    anchors = anchors,
                    getAlong = positionHolder::along,
                    getPosition = positionHolder::position,
                    updateAlong = positionHolder.setAlong,
                    updatePosition = positionHolder.setPosition,
                    getVelocity = positionHolder::velocity,
                    setIsDragging = positionHolder::isDragging::set,
                )
                .drawBehind {

                    val cornerRadius = CornerRadius(cornerRadiusPx)

                    progressFContext?.let { fContext ->
                        val progressRect = Rect(
                            offset = Offset.Zero,
                            size = Size(
                                along = onPositionChanged
                                    .foldNullable(
                                        ifNull = { size.along * positionHolder.fraction },
                                        ifNotNull = { positionHolder.along.along },
                                    )
                                    .coerceAtLeast(cornerRadiusPx * 2),
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
                        val cursorRect = positionHolder.cursorRect
                        drawRoundRect(
                            color = cursorFContext.color,
                            topLeft = cursorRect.topLeft,
                            size = cursorRect.size,
                            cornerRadius = cornerRadius,
                        )
                    }
                },
            anchors = anchors,
            item = { i ->
                Box(
                    modifier = Modifier
                        .option(
                            onPositionChanged
                                ?.takeIf { item != null }
                                ?.let { callback ->
                                    Modifier
                                        .clip(RoundedCornerShape(cornerRadius))
                                        .clickable { callback(i.toFloat()) }
                                }
                        ),
                    propagateMinConstraints = true,
                ) {

                    selectionStates.forEach { selected ->
                        Box(
                            modifier = Modifier.option(
                                drawCursor.ifTrue {
                                    Modifier.clipToCursorRect(
                                        getAnchorRect = { anchors[i].rect },
                                        getCursorRect = { positionHolder.cursorRect },
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

private fun Modifier.clipToCursorRect(
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
private fun Modifier.draggable(
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

@Composable
context(_: Orientation)
private fun SAnchorsLayout(
    anchors: NonEmptyList<Anchor>,
    item: @Composable (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Layout(
        modifier = modifier,
        content = {
            repeat(anchors.size) { i ->
                Box(
                    propagateMinConstraints = true,
                ) {
                    item(i)
                }
            }
        },
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
                placeable.placeRelative(
                    along = alongPosition,
                    across = (across - placeable.across) / 2,
                )
                anchors[i].rect = Rect(
                    offset = Offset(
                        along = alongPosition.toFloat(),
                        across = 0f,
                    ),
                    size = Size(
                        along = placeable.along.toFloat(),
                        across = size.across.toFloat(),
                    )
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

    companion object {

        val VisibilityThreshold: Along
            @Composable
            get() = with(LocalDensity.current) {
                Dp.VisibilityThreshold.toPx().let(::Along)
            }

        val twoWayConverter: TwoWayConverter<Along, AnimationVector1D> = TwoWayConverter(
            convertToVector = { AnimationVector1D(it.along) },
            convertFromVector = { vector -> Along(vector.value) }
        )
    }
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