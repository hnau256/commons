package org.hnau.commons.app.projector.uikit.line.ext

import androidx.compose.ui.geometry.Size
import org.hnau.commons.app.projector.utils.Direction
import org.hnau.commons.app.projector.utils.Orientation
import org.hnau.commons.app.projector.utils.fold
import org.hnau.commons.app.projector.utils.withDirection

fun Size.get(
    orientation: Orientation,
): Float = orientation.fold(
    ifHorizontal = { width },
    ifVertical = { height },
)

context(orientation: Orientation)
fun Size.get(
    direction: Direction,
): Float = get(
    orientation = orientation.withDirection(direction),
)

context(orientation: Orientation)
val Size.along: Float
    get() = get(Direction.Along)

context(orientation: Orientation)
val Size.across: Float
    get() = get(Direction.Across)


context(orientation: Orientation)
fun Size(
    along: Float,
    across: Float,
): Size = orientation.fold(
    ifHorizontal = {
        Size(
            width = along,
            height = across,
        )
    },
    ifVertical = {
        Size(
            width = across,
            height = along,
        )
    }
)