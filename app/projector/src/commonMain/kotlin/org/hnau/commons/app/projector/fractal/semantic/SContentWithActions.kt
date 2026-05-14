package org.hnau.commons.app.projector.fractal.semantic

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.hnau.commons.app.projector.fractal.ForceFill
import org.hnau.commons.app.projector.utils.Orientation

@Composable
fun SContentWithActions(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
    actions: @Composable SActionsScope.() -> Unit,
) {
    SLine(
        modifier = modifier.fillMaxWidth(),
        orientation = Orientation.Vertical,
        forceFill = ForceFill.First,
    ) {
        content()
        SActions(
            modifier = Modifier.fillMaxWidth(),
            block = actions,
        )
    }
}

