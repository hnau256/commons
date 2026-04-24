package org.hnau.commons.app.projector.fractal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding as foundationPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.hnau.commons.app.projector.fractal.utils.OffsetDistance
import org.hnau.commons.app.projector.fractal.utils.color.FractalColorsProvider
import org.hnau.commons.app.projector.fractal.utils.color.getBackgroundColor
import org.hnau.commons.app.projector.fractal.utils.color.local
import org.hnau.commons.app.projector.fractal.utils.localUnits

@Composable
fun FPanel(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {

    OffsetDistance(
        offset = 1,
    ) {
        val units = localUnits
        Box(
            modifier = modifier
                .background(
                    color = FractalColorsProvider.local.getBackgroundColor(),
                    shape = units.shape,
                )
                .foundationPadding(
                    horizontal = units.paddingHorizontal,
                    vertical = units.paddingVertical,
                ),
        ) {
            content()
        }
    }
}