package org.hnau.commons.app.projector.fractal.anchor

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.collectLatest
import kotlin.math.roundToInt

class SAnchorsState(initialPosition: Position = Position(0f)) {

    var position: Position by mutableStateOf(initialPosition)

    var isDragging: Boolean by mutableStateOf(false)

    companion object {

        val saver: Saver<SAnchorsState, Float> = Saver(
            save = { state -> state.position.position },
            restore = { position -> SAnchorsState(Position(position)) },
        )
    }
}

@Composable
fun rememberSAnchorsState(initialPosition: Position = Position(0f)): SAnchorsState =
    rememberSaveable(saver = SAnchorsState.saver) { SAnchorsState(initialPosition) }

@Composable
fun rememberSAnchorsState(
    getSelectedIndex: () -> Int,
    setSelectedIndex: ((Int) -> Unit)?,
): SAnchorsState {

    val currentGetSelectedIndex by rememberUpdatedState(getSelectedIndex)
    val currentSetSelectedIndex by rememberUpdatedState(setSelectedIndex)

    val state = remember {
        SAnchorsState(
            initialPosition = Position(
                currentGetSelectedIndex().toFloat(),
            ),
        )
    }

    LaunchedEffect(state) {
        snapshotFlow { currentGetSelectedIndex() }
            .collectLatest { index ->
                val position = Position(index.toFloat())
                if (position != state.position) {
                    state.position = position
                }
            }
    }

    LaunchedEffect(state) {
        snapshotFlow { state.position to state.isDragging }
            .collectLatest { (position, dragging) ->
                if (dragging) return@collectLatest
                val index = position.position.roundToInt()
                if (currentGetSelectedIndex() != index) {
                    currentSetSelectedIndex?.invoke(index)
                }
            }
    }

    return state
}
