package org.hnau.commons.app.projector.fractal.table

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import org.hnau.commons.app.projector.fractal.SLine
import org.hnau.commons.app.projector.fractal.distance.LocalDistance
import org.hnau.commons.app.projector.fractal.size.units
import org.hnau.commons.app.projector.fractal.table.utils.STableScopeImpl
import org.hnau.commons.app.projector.fractal.utils.LocalShapeCorners
import org.hnau.commons.app.projector.fractal.utils.ShapeCorners
import org.hnau.commons.app.projector.uikit.line.LineScope
import org.hnau.commons.app.projector.utils.Orientation


@Composable
fun STable(
    orientation: Orientation,
    modifier: Modifier = Modifier,
    corners: ShapeCorners.Provider = LocalShapeCorners.current,
    reverseOrdering: Boolean = false,
    content: @Composable STableScope.() -> Unit,
) {
    SLine(
        modifier = modifier,
        orientation = orientation,
        separation = LocalDistance.current.units.borderWidth,
        reverseOrdering = reverseOrdering,
    ) {
        val lineScope: LineScope = this
        val scope = remember(
            orientation,
            corners,
            lineScope,
        ) {
            STableScopeImpl(
                orientation = orientation,
                corners = corners,
                lineScope = lineScope,
            )
        }
        scope.content()
    }
}