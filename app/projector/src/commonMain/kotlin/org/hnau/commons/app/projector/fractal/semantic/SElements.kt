package org.hnau.commons.app.projector.fractal.semantic

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.hnau.commons.app.projector.fractal.FLine
import org.hnau.commons.app.projector.utils.Orientation

@Composable
fun SElements(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    FLine(
        modifier = modifier,
        orientation = Orientation.Vertical,
        content = content,
    )
}