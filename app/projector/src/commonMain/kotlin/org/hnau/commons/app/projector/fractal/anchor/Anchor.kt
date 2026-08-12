package org.hnau.commons.app.projector.fractal.anchor

import androidx.compose.ui.geometry.Rect

data class Anchor(
    val weightBefore: Float,
    var rect: Rect = Rect.Zero,
)