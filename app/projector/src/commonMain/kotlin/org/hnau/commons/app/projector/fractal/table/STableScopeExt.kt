package org.hnau.commons.app.projector.fractal.table

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.hnau.commons.app.projector.utils.opposite

@Composable
fun STableScope.Subtable(
    modifier: Modifier = Modifier,
    reverseOrdering: Boolean = false,
    content: @Composable STableScope.() -> Unit,
) {
    SCell(
        modifier = modifier,
    ) {
        STable(
            orientation = orientation.opposite,
            corners = corners,
            content = content,
            reverseOrdering = reverseOrdering,
        )
    }
}