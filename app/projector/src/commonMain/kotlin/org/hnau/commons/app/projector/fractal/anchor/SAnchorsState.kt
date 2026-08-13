package org.hnau.commons.app.projector.fractal.anchor

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

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
