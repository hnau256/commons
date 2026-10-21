package org.hnau.commons.app.projector.uikit.line.ext

import androidx.compose.ui.unit.IntSize
import org.hnau.commons.app.projector.utils.Direction
import org.hnau.commons.app.projector.utils.Orientation
import org.hnau.commons.app.projector.utils.fold
import org.hnau.commons.app.projector.utils.withDirection

fun IntSize.get(
    orientation: Orientation,
): Int = orientation.fold(
    ifHorizontal = { width },
    ifVertical = { height },
)

context(orientation: Orientation)
fun IntSize.get(
    direction: Direction,
): Int = get(
    orientation = orientation.withDirection(direction),
)

context(orientation: Orientation)
val IntSize.along: Int
    get() = get(Direction.Along)

context(orientation: Orientation)
val IntSize.across: Int
    get() = get(Direction.Across)


context(orientation: Orientation)
fun IntSize(
    along: Int,
    across: Int,
): IntSize = orientation.fold(
    ifHorizontal = {
        IntSize(
            width = along,
            height = across,
        )
    },
    ifVertical = {
        IntSize(
            width = across,
            height = along,
        )
    }
)