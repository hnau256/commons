package org.hnau.commons.app.projector.fractal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import org.hnau.commons.app.projector.fractal.utils.Distance
import org.hnau.commons.app.projector.fractal.utils.LocalDistance
import org.hnau.commons.app.projector.fractal.utils.LocalPalette
import org.hnau.commons.app.projector.fractal.utils.color.PaletteType
import org.hnau.commons.app.projector.fractal.utils.color.localBackground
import org.hnau.commons.app.projector.fractal.utils.color.provider.FractalColorsProvider
import org.hnau.commons.app.projector.fractal.utils.color.provider.LocalFractalColorsProvider
import org.hnau.commons.app.projector.fractal.utils.color.tone.SwitchBackgroundTone
import org.hnau.commons.app.projector.fractal.utils.size.fPadding

@Composable
fun FBase(
    fractalColorsProvider: FractalColorsProvider,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalFractalColorsProvider provides fractalColorsProvider,
        LocalDistance provides Distance.zero,
        LocalPalette provides PaletteType.Neutral,
    ) {
        SwitchBackgroundTone {
            Box(
                modifier = modifier
                    .background(
                        color = Color.localBackground,
                    )
                    .fPadding(),
            ) {
                content()
            }
        }
    }
}