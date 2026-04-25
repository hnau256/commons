package org.hnau.commons.app.projector.fractal.utils.size

import androidx.compose.ui.unit.Dp

data class Spaces(
    val medium: Dp,
) {

    val large: Dp
        get() = medium * 2

    val small: Dp
        get() = medium / 2

    val extraSmall: Dp
        get() = small / 2
}