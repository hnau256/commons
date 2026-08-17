package org.hnau.commons.app.projector.fractal.anchor

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.collectLatest
import kotlin.math.roundToInt

class SAnchorsState(
    initialPosition: Position = Position(0f),
) {

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
private fun SAnchorsState.Companion.rememberForPosition(
    getPosition: () -> Float,
    setPosition: ((Float) -> Unit)?,
): SAnchorsState {

    val currentGetPosition by rememberUpdatedState(getPosition)
    val currentSetPosition by rememberUpdatedState(setPosition)

    val state = remember {
        SAnchorsState(
            initialPosition = Position(
                currentGetPosition(),
            ),
        )
    }

    LaunchedEffect(state) {
        snapshotFlow { currentGetPosition() }
            .collectLatest { index ->
                val position = Position(index)
                if (position != state.position) {
                    state.position = position
                }
            }
    }

    LaunchedEffect(state) {
        snapshotFlow { state.position to state.isDragging }
            .collectLatest { (position, dragging) ->
                if (dragging) return@collectLatest
                val position = position.position
                if (currentGetPosition() != position) {
                    currentSetPosition?.invoke(position)
                }
            }
    }

    return state
}

@Composable
fun SAnchorsState.Companion.rememberForFraction(
    getFraction: () -> Float,
    setFraction: ((Float) -> Unit)?,
): SAnchorsState = rememberForPosition(
    getPosition = getFraction,
    setPosition = setFraction,
)

@Composable
fun SAnchorsState.Companion.rememberForIndex(
    getSelectedIndex: () -> Int,
    setSelectedIndex: ((Int) -> Unit)?,
): SAnchorsState = rememberForPosition(
    getPosition = { getSelectedIndex().toFloat() },
    setPosition = setSelectedIndex?.let { set ->
        { index -> set(index.roundToInt()) }
    }
)