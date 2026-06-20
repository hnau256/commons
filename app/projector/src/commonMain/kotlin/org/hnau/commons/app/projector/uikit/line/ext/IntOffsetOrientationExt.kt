package org.hnau.commons.app.projector.uikit.line.ext

import androidx.compose.ui.unit.IntOffset
import org.hnau.commons.app.projector.utils.Direction
import org.hnau.commons.app.projector.utils.Orientation
import org.hnau.commons.app.projector.utils.fold
import org.hnau.commons.app.projector.utils.withDirection

fun IntOffset.get(
    orientation: Orientation,
): Int = orientation.fold(
    ifHorizontal = { x },
    ifVertical = { y },
)

context(orientation: Orientation)
fun IntOffset.get(
    direction: Direction,
): Int = get(
    orientation = orientation.withDirection(direction),
)

context(orientation: Orientation)
val IntOffset.along: Int
    get() = get(Direction.Along)

context(orientation: Orientation)
val IntOffset.across: Int
    get() = get(Direction.Across)


context(orientation: Orientation)
fun IntOffset(
    along: Int,
    across: Int,
): IntOffset = orientation.fold(
    ifHorizontal = {
        IntOffset(
            x = along,
            y = across,
        )
    },
    ifVertical = {
        IntOffset(
            x = across,
            y = along,
        )
    }
)