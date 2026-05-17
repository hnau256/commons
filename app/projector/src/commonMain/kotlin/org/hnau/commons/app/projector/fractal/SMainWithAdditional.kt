package org.hnau.commons.app.projector.fractal

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.hnau.commons.app.projector.utils.Orientation

@Composable
fun SMainWithAdditional(
    modifier: Modifier = Modifier,
    main: @Composable () -> Unit,
    additional: @Composable () -> Unit,
) {
    SLine(
        modifier = modifier,
        orientation = Orientation.Vertical,
        forceFill = ForceFill.Last,
    ) {
        SPanel(
            modifier = Modifier.fillMaxWidth(),
            content = additional,
        )
        main()
    }
}