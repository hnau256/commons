package org.hnau.commons.app.projector.fractal.anchor

import androidx.compose.animation.core.VectorConverter
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

@Composable
internal fun rememberSAnchorsCursor(
    state: SAnchorsState,
    calcRectByPosition: (Position) -> Rect,
): State<Rect> {

    val result = remember(
        calcRectByPosition,
        state,
    ) { mutableStateOf(calcRectByPosition(state.position)) }

    LaunchedEffect(calcRectByPosition, state) {
        snapshotFlow {
            state.position to state.isDragging
        }.collectLatest { (position, dragging) ->
            val target = calcRectByPosition(position)
            if (target == result.value) return@collectLatest
            dragging.foldBoolean(
                ifTrue = {
                    result.value = target
                },
                ifFalse = {
                    animate(
                        initialValue = result.value,
                        targetValue = target,
                        typeConverter = Rect.VectorConverter,
                        animationSpec = spring(),
                    ) { value, _ ->
                        result.value = value
                    }
                },
            )
        }
    }
    return result
}
