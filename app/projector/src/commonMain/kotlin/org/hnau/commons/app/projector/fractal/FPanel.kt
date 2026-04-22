package org.hnau.commons.app.projector.fractal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.hnau.commons.app.projector.fractal.utils.OffsetDistance
import org.hnau.commons.app.projector.fractal.utils.color.FractalColorsProvider
import org.hnau.commons.app.projector.fractal.utils.color.getBackgroundColor
import org.hnau.commons.app.projector.fractal.utils.color.local
import org.hnau.commons.app.projector.fractal.utils.localShape
import org.hnau.commons.app.projector.fractal.utils.padding

@Composable
fun FPanel(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {

    OffsetDistance(
        offset = 1,
    ) {
        Box(
            modifier = modifier
                .background(
                    color = FractalColorsProvider.local.getBackgroundColor(),
                    shape = localShape,
                )
                .padding(),
        ) {
            content()
        }
    }
}