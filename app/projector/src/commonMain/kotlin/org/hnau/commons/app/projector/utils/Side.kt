package org.hnau.commons.app.projector.utils

import org.hnau.commons.gen.enumvalues.annotations.EnumValues

@EnumValues
enum class Side {
    Start, Top, End, Bottom;

    companion object
}

val Side.orientation: Orientation
    get() = fold(
        ifStart = { Orientation.Horizontal },
        ifTop = { Orientation.Vertical },
        ifEnd = { Orientation.Horizontal },
        ifBottom = { Orientation.Vertical }
    )

val Side.opposite: Side
    get() = fold(
        ifStart = { Side.End },
        ifTop = { Side.Bottom },
        ifEnd = { Side.Start },
        ifBottom = { Side.Top }
    )

inline fun <R> Side.fold(
    ifStart: () -> R,
    ifTop: () -> R,
    ifEnd: () -> R,
    ifBottom: () -> R,
): R = when (this) {
    Side.Start -> ifStart()
    Side.Top -> ifTop()
    Side.End -> ifEnd()
    Side.Bottom -> ifBottom()
}