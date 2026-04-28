package org.hnau.commons.app.projector.utils

import org.hnau.commons.gen.enumvalues.annotations.EnumValues
import org.hnau.commons.kotlin.foldBoolean

@EnumValues
enum class Direction {
    Along, Across;

    companion object
}

val Direction.opposite: Direction
    get() = fold(
        ifAlong = { Direction.Across },
        ifAcross = { Direction.Along },
    )

inline fun <R> Direction.fold(
    ifAlong: () -> R,
    ifAcross: () -> R,
): R = when (this) {
    Direction.Along -> ifAlong()
    Direction.Across -> ifAcross()
}

fun Orientation.withDirection(
    direction: Direction,
): Orientation = direction.fold(
    ifAlong = { this },
    ifAcross = { opposite },
)

fun Orientation.compareWith(
    other: Orientation,
): Direction = (this == other).foldBoolean(
    ifTrue = { Direction.Along },
    ifFalse = { Direction.Across },
)