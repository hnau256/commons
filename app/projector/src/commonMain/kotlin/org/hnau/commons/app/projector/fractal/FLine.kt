package org.hnau.commons.app.projector.fractal

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import org.hnau.commons.app.projector.fractal.utils.size.FUnits
import org.hnau.commons.app.projector.utils.Orientation

@Composable
fun FLine(
    orientation: Orientation,
    modifier: Modifier = Modifier,
    separation: Dp = FUnits.local.padding[orientation].medium,
    alignment: Alignment.Horizontal = Alignment.Start,
    reverseOrdering: Boolean = false,
    forceFill: ForceFill = ForceFill.default,
    content: @Composable () -> Unit,
) {
    Line(
        modifier = modifier,
        orientation = orientation,
        arrangement = Arrangement.spacedBy(
            space = separation,
            alignment = alignment,
        ),
        reverseOrdering = reverseOrdering,
        forceFill = forceFill,
        content = content,
    )
}