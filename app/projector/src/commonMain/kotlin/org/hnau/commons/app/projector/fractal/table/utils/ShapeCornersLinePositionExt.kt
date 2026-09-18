package org.hnau.commons.app.projector.fractal.table.utils

import org.hnau.commons.app.projector.fractal.utils.ShapeCorners
import org.hnau.commons.app.projector.uikit.line.LinePosition
import org.hnau.commons.app.projector.utils.Orientation

internal fun ShapeCorners.Provider.closeAt(
    orientation: Orientation,
    position: LinePosition,
): ShapeCorners.Provider = closeAt(orientation) { position }

internal fun ShapeCorners.Provider.closeAt(
    orientation: Orientation,
    position: () -> LinePosition,
): ShapeCorners.Provider = ShapeCorners.Provider {
    val pos = position()
    getTableCorners().close(
        orientation = orientation,
        startOrTop = !pos.isFirst,
        endOrBottom = !pos.isLast,
    )
}
