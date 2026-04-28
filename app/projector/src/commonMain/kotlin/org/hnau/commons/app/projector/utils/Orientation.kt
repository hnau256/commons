package org.hnau.commons.app.projector.utils

import org.hnau.commons.gen.enumvalues.annotations.EnumValues

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

inline fun <R> Orientation.fold(
    ifHorizontal: () -> R,
    ifVertical: () -> R,
): R = when (this) {
    Orientation.Horizontal -> ifHorizontal()
    Orientation.Vertical -> ifVertical()
}