package org.hnau.commons.app.projector.fractal.semantic

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.hnau.commons.app.projector.fractal.FLine
import org.hnau.commons.app.projector.fractal.FPanel
import org.hnau.commons.app.projector.fractal.ForceFill
import org.hnau.commons.app.projector.fractal.utils.size.SpaceSize
import org.hnau.commons.app.projector.utils.Orientation

@Composable
fun SMainWithAdditional(
    modifier: Modifier = Modifier,
    main: @Composable () -> Unit,
    additional: @Composable () -> Unit,
) {
    FLine(
        modifier = modifier,
        orientation = Orientation.Vertical,
        forceFill = ForceFill.Last,
        separation = SpaceSize.Large,
    ) {
        FPanel(
            modifier = Modifier.fillMaxWidth(),
            content = additional,
        )
        main()
    }
}