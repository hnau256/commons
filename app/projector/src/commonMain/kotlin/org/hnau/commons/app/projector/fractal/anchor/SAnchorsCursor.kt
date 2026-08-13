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
internal fun rememberSAnchorsCursorAlong(
    state: SAnchorsState,
    mapper: SAnchorsMapper,
): State<Along> {
    val along = remember(mapper, state) { mutableStateOf(mapper.direct(state.position)) }
    LaunchedEffect(mapper, state) {
        snapshotFlow { state.position to state.isDragging }
            .collectLatest { (position, dragging) ->
                val target = mapper.direct(position)
                if (target == along.value) return@collectLatest
                dragging.foldBoolean(
                    ifTrue = {
                        along.value = target
                    },
                    ifFalse = {
                        animate(
                            initialValue = along.value,
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
    return along
}
