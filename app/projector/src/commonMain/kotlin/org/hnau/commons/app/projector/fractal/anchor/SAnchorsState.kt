package org.hnau.commons.app.projector.fractal.anchor

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

class SAnchorsState(initialPosition: Position = Position(0f)) {
    var position: Position by mutableStateOf(initialPosition)
    var isDragging: Boolean by mutableStateOf(false)
}

@Composable
fun rememberSAnchorsState(initialPosition: Position = Position(0f)): SAnchorsState =
    remember { SAnchorsState(initialPosition) }
