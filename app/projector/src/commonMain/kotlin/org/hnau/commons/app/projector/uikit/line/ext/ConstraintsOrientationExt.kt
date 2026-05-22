package org.hnau.commons.app.projector.uikit.line.ext

import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.unit.constrainWidth
import androidx.compose.ui.unit.offset
import org.hnau.commons.app.projector.utils.Direction
import org.hnau.commons.app.projector.utils.Orientation
import org.hnau.commons.app.projector.utils.fold
import org.hnau.commons.app.projector.utils.withDirection

fun Constraints.max(
    orientation: Orientation,
): Int? = orientation.fold(
    ifHorizontal = { maxWidth.takeIf { hasBoundedWidth } },
    ifVertical = { maxHeight.takeIf { hasBoundedHeight } },
)

context(orientation: Orientation)
fun Constraints.max(
    direction: Direction,
): Int? = max(
    orientation = orientation.withDirection(direction)
)

context(orientation: Orientation)
val Constraints.maxAlong: Int?
    get() = max(Direction.Along)

context(orientation: Orientation)
val Constraints.maxAcross: Int?
    get() = max(Direction.Across)

fun Constraints.min(
    orientation: Orientation,
): Int = orientation.fold(
    ifHorizontal = { minWidth },
    ifVertical = { minHeight },
)

context(orientation: Orientation)
fun Constraints.min(
    direction: Direction,
): Int = min(
    orientation = orientation.withDirection(direction)
)

context(orientation: Orientation)
val Constraints.minAlong: Int
    get() = min(Direction.Along)

context(orientation: Orientation)
val Constraints.minAcross: Int
    get() = min(Direction.Across)


context(orientation: Orientation)
fun Constraints.constrain(
    direction: Direction,
    value: Int,
): Int = orientation.withDirection(direction).fold(
    ifHorizontal = { constrainWidth(value) },
    ifVertical = { constrainHeight(value) },
)

context(orientation: Orientation)
fun Constraints.constrainAcross(
    across: Int,
): Int = constrain(
    direction = Direction.Across,
    value = across,
)

context(orientation: Orientation)
fun Constraints.constrainAlong(
    along: Int,
): Int = constrain(
    direction = Direction.Along,
    value = along,
)


context(orientation: Orientation)
fun Constraints.constrain(
    along: Int,
    across: Int,
): IntSize = orientation.fold(
    ifHorizontal = {
        IntSize(
            width = constrainWidth(along),
            height = constrainHeight(across),
        )
    },
    ifVertical = {
        IntSize(
            width = constrainWidth(across),
            height = constrainHeight(along),
        )
    },
)


context(orientation: Orientation)
fun Constraints.offset(
    along: Int = 0,
    across: Int = 0,
): Constraints = orientation.fold(
    ifHorizontal = {
        offset(
            horizontal = along,
            vertical = across,
        )
    },
    ifVertical = {
        offset(
            horizontal = across,
            vertical = along,
        )
    },
)


fun Constraints.withoutMin(
    orientation: Orientation,
): Constraints = orientation.fold(
    ifHorizontal = { copy(minWidth = 0) },
    ifVertical = { copy(minHeight = 0) },
)

context(orientation: Orientation)
fun Constraints.withoutMin(
    direction: Direction,
): Constraints = withoutMin(
    orientation = orientation.withDirection(direction),
)

context(orientation: Orientation)
fun Constraints.withoutMinAlong(): Constraints =
    withoutMin(Direction.Along)

context(orientation: Orientation)
fun Constraints.withoutMinAcross(): Constraints =
    withoutMin(Direction.Across)


fun Constraints.withoutMax(
    orientation: Orientation,
): Constraints = orientation.fold(
    ifHorizontal = { copy(maxWidth = Constraints.Infinity) },
    ifVertical = { copy(maxHeight = Constraints.Infinity) },
)

context(orientation: Orientation)
fun Constraints.withoutMax(
    direction: Direction,
): Constraints = withoutMax(
    orientation = orientation.withDirection(direction),
)

context(orientation: Orientation)
fun Constraints.withoutMaxAlong(): Constraints =
    withoutMax(Direction.Along)

context(orientation: Orientation)
fun Constraints.withoutMaxAcross(): Constraints =
    withoutMax(Direction.Across)

context(orientation: Orientation)
fun Constraints.Companion.fixed(
    along: Int,
    across: Int,
): Constraints = orientation.fold(
    ifHorizontal = {
        Constraints.fixed(
            width = along,
            height = across,
        )
    },
    ifVertical = {
        Constraints.fixed(
            width = across,
            height = along,
        )
    }
)

context(orientation: Orientation)
fun Constraints.copy(
    minAlong: Int = this.minAlong,
    maxAlong: Int? = this.maxAlong,
    minAcross: Int = this.minAcross,
    maxAcross: Int? = this.maxAcross,
): Constraints = Constraints(
    minAlong = minAlong,
    maxAlong = maxAlong,
    minAcross = minAcross,
    maxAcross = maxAcross,
)

context(orientation: Orientation)
fun Constraints(
    minAlong: Int = 0,
    maxAlong: Int? = null,
    minAcross: Int = 0,
    maxAcross: Int? = null,
): Constraints = orientation.fold(
    ifHorizontal = {
        Constraints(
            minWidth = minAlong,
            maxWidth = maxAlong ?: Constraints.Infinity,
            minHeight = minAcross,
            maxHeight = maxAcross ?: Constraints.Infinity,
        )
    },
    ifVertical = {
        Constraints(
            minHeight = minAlong,
            maxHeight = maxAlong ?: Constraints.Infinity,
            minWidth = minAcross,
            maxWidth = maxAcross ?: Constraints.Infinity,
        )
    }
)





