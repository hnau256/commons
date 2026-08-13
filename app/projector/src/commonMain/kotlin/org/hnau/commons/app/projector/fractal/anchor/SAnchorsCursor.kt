package org.hnau.commons.app.projector.fractal.anchor

import androidx.compose.animation.core.animate
import androidx.compose.animation.core.spring
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.collectLatest
import org.hnau.commons.kotlin.foldBoolean

@Composable
internal fun rememberSAnchorsCursor(
    state: SAnchorsState,
    calcRectCenterAlongPxByPosition: (Position) -> RectCenterAlongPx,
): State<RectCenterAlongPx> {

    val along = remember(
        calcRectCenterAlongPxByPosition,
        state,
    ) { mutableStateOf(calcRectCenterAlongPxByPosition(state.position)) }

    LaunchedEffect(calcRectCenterAlongPxByPosition, state) {
        snapshotFlow {
            state.position to state.isDragging
        }.collectLatest { (position, dragging) ->
            val target = calcRectCenterAlongPxByPosition(position)
            if (target == along.value) return@collectLatest
            dragging.foldBoolean(
                ifTrue = {
                    along.value = target
                },
                ifFalse = {
                    animate(
                        initialValue = along.value,
                        targetValue = target,
                        typeConverter = RectCenterAlongPx.twoWayConverter,
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
