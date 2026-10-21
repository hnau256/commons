package org.hnau.commons.app.projector.uikit.line.ext

import androidx.compose.ui.layout.Placeable
import org.hnau.commons.app.projector.utils.Direction
import org.hnau.commons.app.projector.utils.Orientation
import org.hnau.commons.app.projector.utils.fold
import org.hnau.commons.app.projector.utils.withDirection

fun Placeable.get(
    orientation: Orientation,
): Int = orientation.fold(
    ifHorizontal = { width },
    ifVertical = { height },
)

context(orientation: Orientation)
fun Placeable.get(
    direction: Direction,
): Int = get(
    orientation = orientation.withDirection(direction),
)

context(orientation: Orientation)
val Placeable.along: Int
    get() = get(Direction.Along)

context(orientation: Orientation)
val Placeable.across: Int
    get() = get(Direction.Across)

context(orientation: Orientation, scope: Placeable.PlacementScope)
fun Placeable.placeRelative(
    along: Int,
    across: Int,
    zIndex: Float = 0f,
) {
    with(scope) {
        orientation.fold(
            ifHorizontal = {
                placeRelative(
                    x = along,
                    y = across,
                    zIndex = zIndex,
                )
            },
            ifVertical = {
                placeRelative(
                    x = across,
                    y = along,
                    zIndex = zIndex,
                )
            }
        )
    }
}