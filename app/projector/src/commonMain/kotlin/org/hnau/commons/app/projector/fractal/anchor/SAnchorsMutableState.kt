package org.hnau.commons.app.projector.fractal.anchor

import androidx.compose.animation.core.animate
import androidx.compose.animation.core.spring
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.lerp
import androidx.compose.ui.input.pointer.util.VelocityTracker
import arrow.core.NonEmptyList
import arrow.core.toNonEmptyListOrThrow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.hnau.commons.app.projector.uikit.line.ext.Offset
import org.hnau.commons.app.projector.uikit.line.ext.along
import org.hnau.commons.app.projector.utils.Orientation
import org.hnau.commons.app.projector.utils.observe
import org.hnau.commons.kotlin.Mutable
import org.hnau.commons.kotlin.coroutines.createChild
import org.hnau.commons.kotlin.foldBoolean
import org.hnau.commons.kotlin.foldNullable
import org.hnau.commons.kotlin.ifTrue
import org.hnau.commons.kotlin.mapper.Mapper
import kotlin.time.Clock

@Composable
fun rememberSAnchorsState(
    orientation: Orientation,
    weights: NonEmptyList<Float>,
    getPosition: () -> Float,
    onPositionChanged: (Position) -> Unit,
): SAnchorsMutableState {
    val alongVisibilityThreshold by rememberUpdatedState(Along.VisibilityThreshold)
    val onPositionChangedState by rememberUpdatedState(onPositionChanged)
    val coroutineScope = rememberCoroutineScope { Dispatchers.Unconfined }
    val childScopeRef = remember { Mutable<CoroutineScope?>(null) }

    return remember(weights) {
        childScopeRef.value?.cancel()

        val anchors = buildList {
            add(Anchor(weightBefore = 0f))
            val atLeastOneWeight = weights.any { weight -> weight > 0 }
            addAll(
                weights.map { weight ->
                    Anchor(
                        weightBefore = atLeastOneWeight.foldBoolean(
                            ifTrue = { weight },
                            ifFalse = { 1f },
                        ),
                    )
                },
            )
        }.toNonEmptyListOrThrow()

        val scope = coroutineScope.createChild()
        childScopeRef.value = scope

        SAnchorsMutableState(
            orientation = orientation,
            anchors = anchors,
            scope = scope,
            getPosition = { getPosition().let(::Position) },
            getAlongVisibilityThreshold = { alongVisibilityThreshold },
            onPositionChanged = onPositionChangedState,
        )
    }
}

class SAnchorsMutableState internal constructor(
    scope: CoroutineScope,
    private val orientation: Orientation,
    override val anchors: NonEmptyList<Anchor>,
    getPosition: () -> Position,
    private val getAlongVisibilityThreshold: () -> Along,
    private val onPositionChanged: (Position) -> Unit,
) : SAnchorsState, SAnchorsStateMediator {

    private fun getRect(position: Position): Rect {
        val fromIndex = position.position.toInt()
        val from = anchors[fromIndex.coerceIn(0, anchors.lastIndex)].rect
        val to = anchors[(fromIndex + 1).coerceIn(0, anchors.lastIndex)].rect
        return when {
            from == to -> from
            else -> lerp(
                start = from,
                stop = to,
                fraction = position.position - fromIndex,
            )
        }
    }

    val positionAlongMapper: Mapper<Position, Along> = Mapper(
        direct = { position ->
            with(orientation) {
                position.let(::getRect).center.along.let(::Along)
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
        },
    )

    override var isDragging: Boolean by mutableStateOf(false)

    private val alongRaw: Along
            by derivedStateOf { getPosition().let(positionAlongMapper.direct) }

    override var along: Along by mutableStateOf(alongRaw)
        private set

    override val position: Position by derivedStateOf {
        along.let(positionAlongMapper.reverse)
    }

    override val cursorRect: Rect
        get() = getRect(position)

    override fun updateAlong(along: Along) {
        onPositionChanged(along.let(positionAlongMapper.reverse))
    }

    override fun updatePosition(position: Position) {
        onPositionChanged(position)
    }

    override fun setIsDragging(isDragging: Boolean) {
        this.isDragging = isDragging
    }

    private val velocityTracker = VelocityTracker()

    override val velocity: Along
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
                                Offset(along = along.along, across = 0f)
                            },
                        )
                    },
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