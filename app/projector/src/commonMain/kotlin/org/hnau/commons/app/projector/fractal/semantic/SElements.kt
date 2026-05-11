package org.hnau.commons.app.projector.fractal.semantic

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.hnau.commons.app.projector.utils.Orientation

@Composable
fun SElements(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    SLine(
        modifier = modifier.verticalScroll(rememberScrollState()),
        orientation = Orientation.Vertical,
        content = content,
    )
}