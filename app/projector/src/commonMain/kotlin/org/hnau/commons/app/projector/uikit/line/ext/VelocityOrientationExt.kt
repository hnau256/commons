package org.hnau.commons.app.projector.uikit.line.ext

import androidx.compose.ui.unit.Velocity
import org.hnau.commons.app.projector.utils.Direction
import org.hnau.commons.app.projector.utils.Orientation
import org.hnau.commons.app.projector.utils.fold
import org.hnau.commons.app.projector.utils.withDirection

fun Velocity.get(
    orientation: Orientation,
): Float = orientation.fold(
    ifHorizontal = { x },
    ifVertical = { y },
)

context(orientation: Orientation)
fun Velocity.get(
    direction: Direction,
): Float = get(
    orientation = orientation.withDirection(direction),
)

context(orientation: Orientation)
val Velocity.along: Float
    get() = get(Direction.Along)

context(orientation: Orientation)
val Velocity.across: Float
    get() = get(Direction.Across)


context(orientation: Orientation)
fun Velocity(
    along: Float,
    across: Float,
): Velocity = orientation.fold(
    ifHorizontal = {
        Velocity(
            x = along,
            y = across,
        )
    },
    ifVertical = {
        Velocity(
            x = across,
            y = along,
        )
    }
)