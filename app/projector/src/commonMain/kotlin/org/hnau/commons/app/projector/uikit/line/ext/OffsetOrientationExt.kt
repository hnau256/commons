package org.hnau.commons.app.projector.uikit.line.ext

import androidx.compose.ui.geometry.Offset
import org.hnau.commons.app.projector.utils.Direction
import org.hnau.commons.app.projector.utils.Orientation
import org.hnau.commons.app.projector.utils.fold
import org.hnau.commons.app.projector.utils.withDirection

fun Offset.get(
    orientation: Orientation,
): Float = orientation.fold(
    ifHorizontal = { x },
    ifVertical = { y },
)

context(orientation: Orientation)
fun Offset.get(
    direction: Direction,
): Float = get(
    orientation = orientation.withDirection(direction),
)

context(orientation: Orientation)
val Offset.along: Float
    get() = get(Direction.Along)

context(orientation: Orientation)
val Offset.across: Float
    get() = get(Direction.Across)


context(orientation: Orientation)
fun Offset(
    along: Float,
    across: Float,
): Offset = orientation.fold(
    ifHorizontal = {
        Offset(
            x = along,
            y = across,
        )
    },
    ifVertical = {
        Offset(
            x = across,
            y = along,
        )
    }
)