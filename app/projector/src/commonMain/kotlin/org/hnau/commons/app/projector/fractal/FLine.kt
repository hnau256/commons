package org.hnau.commons.app.projector.fractal

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.hnau.commons.app.projector.fractal.context.LocalFContext
import org.hnau.commons.app.projector.fractal.size.SizeType
import org.hnau.commons.app.projector.fractal.size.units
import org.hnau.commons.app.projector.utils.Orientation

@Composable
fun FLine(
    orientation: Orientation,
    modifier: Modifier = Modifier,
    separation: SizeType = SizeType.Medium,
    alignment: Alignment.Horizontal = Alignment.Start,
    reverseOrdering: Boolean = false,
    forceFill: ForceFill? = null,
    content: @Composable () -> Unit,
) {
    Line(
        modifier = modifier,
        orientation = orientation,
        arrangement = Arrangement.spacedBy(
            space = LocalFContext.current.distance.units.padding.along[separation],
            alignment = alignment,
        ),
        reverseOrdering = reverseOrdering,
        forceFill = forceFill,
        content = content,
    )
}