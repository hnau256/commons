package org.hnau.commons.app.projector.utils

import org.hnau.commons.gen.enumvalues.annotations.EnumValues
import org.hnau.commons.gen.fold.annotations.Fold
import org.hnau.commons.kotlin.foldBoolean

@Fold
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