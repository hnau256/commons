package org.hnau.commons.app.projector.fractal.semantic

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.hnau.commons.app.projector.fractal.FColumn
import org.hnau.commons.app.projector.fractal.FPanel

@Composable
fun SMainWithAdditional(
    modifier: Modifier = Modifier,
    main: @Composable () -> Unit,
    additional: @Composable () -> Unit,
) {
    FColumn(
        modifier = modifier,
    ) {
        FPanel(
            modifier = modifier.fillMaxWidth(),
            content = additional,
        )
        main()
    }
}