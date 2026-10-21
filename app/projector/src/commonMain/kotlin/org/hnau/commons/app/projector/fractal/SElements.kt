package org.hnau.commons.app.projector.fractal

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.hnau.commons.app.projector.fractal.distance.LocalDistance
import org.hnau.commons.app.projector.fractal.size.units
import org.hnau.commons.app.projector.uikit.line.LineScope
import org.hnau.commons.app.projector.utils.Orientation

@Composable
fun SElements(
    modifier: Modifier = Modifier,
    content: @Composable LineScope.() -> Unit,
) {
    SLine(
        modifier = modifier,//.verticalScroll(rememberScrollState()),
        orientation = Orientation.Vertical,
        separation = LocalDistance.current.units.padding.along.medium,
        content = content,
    )
}