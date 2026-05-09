package org.hnau.commons.app.projector.fractal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.hnau.commons.app.projector.fractal.context.LocalFContext
import org.hnau.commons.app.projector.fractal.context.UpdateFContext
import org.hnau.commons.app.projector.fractal.size.fPadding
import org.hnau.commons.app.projector.fractal.size.units

@Composable
fun FPanel(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    UpdateFContext(
        update = {
            makeDeeper(
                offset = 1,
                resetOverlay = true,
            )
        }
    ) {
        val fContext = LocalFContext.current
        Box(
            modifier = modifier
                .background(
                    color = fContext.containerColor,
                    shape = fContext.distance.units.shape,
                )
                .fPadding(),
            propagateMinConstraints = true,
        ) {
            content()
        }
    }
}