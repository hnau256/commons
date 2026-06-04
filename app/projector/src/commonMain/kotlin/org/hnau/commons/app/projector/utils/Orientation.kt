package org.hnau.commons.app.projector.utils

import org.hnau.commons.gen.enumvalues.annotations.EnumValues
import org.hnau.commons.gen.fold.annotations.Fold

@Fold
@EnumValues
enum class Orientation {
    Horizontal, Vertical;

    companion object
}

val Orientation.opposite: Orientation
    get() = fold(
        ifHorizontal = { Orientation.Vertical },
        ifVertical = { Orientation.Horizontal },
    )