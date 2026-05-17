package org.hnau.commons.app.projector.fractal

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.hnau.commons.app.projector.fractal.size.SizeType
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
        separation = SizeType.Medium,
    ) {
        content()
        SActions(
            modifier = Modifier.fillMaxWidth(),
            block = actions,
        )
    }
}

