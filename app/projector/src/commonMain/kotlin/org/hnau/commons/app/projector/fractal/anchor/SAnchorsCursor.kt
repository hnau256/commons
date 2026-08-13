package org.hnau.commons.app.projector.fractal.anchor

import androidx.compose.animation.core.animate
import androidx.compose.animation.core.spring
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.geometry.Rect
import kotlinx.coroutines.flow.collectLatest
import org.hnau.commons.kotlin.foldBoolean
import org.hnau.commons.kotlin.mapper.Mapper

@Composable
internal fun rememberSAnchorsCursor(
    state: SAnchorsState,
    calcRectByPosition: (Position) -> Rect,
): State<AlongPx> {
    val along =
        remember(calcRectByPosition, state) { mutableStateOf(calcRectByPosition(state.position)) }
    LaunchedEffect(calcRectByPosition, state) {
        snapshotFlow { state.position to state.isDragging }
            .collectLatest { (position, dragging) ->
                val target = calcRectByPosition(position)
                if (target == along.value) return@collectLatest
                dragging.foldBoolean(
                    ifTrue = {
                        along.value = target
                    },
                    ifFalse = {
                        animate(
                            initialValue = along.value,
                            targetValue = target,
                            typeConverter = AlongPx.twoWayConverter,
                            animationSpec = spring(),
                        ) { value, _ ->
                            along.value = value
                        }
                    },
                )
            }
    }
    return along
}
