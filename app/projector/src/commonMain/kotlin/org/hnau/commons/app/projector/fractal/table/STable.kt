package org.hnau.commons.app.projector.fractal.table

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import org.hnau.commons.app.projector.fractal.context.LocalFContext
import org.hnau.commons.app.projector.fractal.size.units
import org.hnau.commons.app.projector.uikit.line.Line
import org.hnau.commons.app.projector.uikit.line.LineScope
import org.hnau.commons.app.projector.fractal.table.utils.STableScopeImpl
import org.hnau.commons.app.projector.utils.Orientation


@Composable
fun STable(
    orientation: Orientation,
    modifier: Modifier = Modifier,
    corners: STableCorners.Provider = STableCorners.Provider.opened,
    reverseOrdering: Boolean = false,
    content: @Composable STableScope.() -> Unit,
) {
    Line(
        modifier = modifier,
        orientation = orientation,
        separation = LocalFContext.current.distance.units.borderWidth,
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