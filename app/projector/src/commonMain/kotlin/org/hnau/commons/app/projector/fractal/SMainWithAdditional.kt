package org.hnau.commons.app.projector.fractal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import org.hnau.commons.app.projector.uikit.line.Line
import org.hnau.commons.app.projector.uikit.line.weight
import org.hnau.commons.app.projector.utils.Orientation

@Composable
fun SMainWithAdditional(
    modifier: Modifier = Modifier,
    main: @Composable () -> Unit,
    additional: @Composable () -> Unit,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        SPanel(
            modifier = Modifier.fillMaxWidth(),
            content = additional,
        )
        Box(
            modifier = Modifier.fillMaxWidth(),
            propagateMinConstraints = true,
        ) {
            main()
        }
    }
}