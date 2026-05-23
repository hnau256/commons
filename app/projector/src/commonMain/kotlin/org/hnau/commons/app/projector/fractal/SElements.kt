package org.hnau.commons.app.projector.fractal

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.hnau.commons.app.projector.fractal.context.LocalFContext
import org.hnau.commons.app.projector.fractal.size.units
import org.hnau.commons.app.projector.uikit.line.Line
import org.hnau.commons.app.projector.uikit.line.LineScope
import org.hnau.commons.app.projector.utils.Orientation

@Composable
fun SElements(
    modifier: Modifier = Modifier,
    content: @Composable LineScope.() -> Unit,
) {
    Line(
        modifier = modifier,//.verticalScroll(rememberScrollState()),
        orientation = Orientation.Vertical,
        separation = LocalFContext.current.distance.units.padding.along.medium,
        content = content,
    )
}