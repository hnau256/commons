package org.hnau.commons.app.projector.fractal.anchor

import androidx.compose.ui.geometry.Rect
import arrow.core.NonEmptyList

interface SAnchorsState {
    val anchors: NonEmptyList<Anchor>
    val along: Along
    val position: Position
    val cursorRect: Rect
    val velocity: Along
    val isDragging: Boolean
}