package org.hnau.commons.app.projector.fractal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import org.hnau.commons.app.projector.fractal.utils.Distance
import org.hnau.commons.app.projector.fractal.utils.LocalDistance
import org.hnau.commons.app.projector.fractal.utils.color.FractalColorsProvider
import org.hnau.commons.app.projector.fractal.utils.color.LocalFractalColorsProvider
import org.hnau.commons.app.projector.fractal.utils.color.getBackgroundColor
import org.hnau.commons.app.projector.fractal.utils.color.local
import org.hnau.commons.app.projector.fractal.utils.localUnits
import androidx.compose.foundation.layout.padding as foundationPadding

@Composable
fun FBase(
    fractalColorsProvider: FractalColorsProvider,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalFractalColorsProvider provides fractalColorsProvider,
        LocalDistance provides Distance.zero,
    ) {
        val units = localUnits
        Box(
            modifier = modifier
                .background(
                    color = FractalColorsProvider.local.getBackgroundColor(),
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