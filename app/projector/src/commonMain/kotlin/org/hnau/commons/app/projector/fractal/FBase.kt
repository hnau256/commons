package org.hnau.commons.app.projector.fractal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import org.hnau.commons.app.model.theme.palette.PaletteType
import org.hnau.commons.app.model.theme.palette.Palettes
import org.hnau.commons.app.projector.fractal.utils.Distance
import org.hnau.commons.app.projector.fractal.utils.LocalDistance
import org.hnau.commons.app.projector.fractal.utils.LocalPalette
import org.hnau.commons.app.projector.fractal.utils.color.localBackground
import org.hnau.commons.app.projector.fractal.utils.color.tone.SwitchBackgroundTone
import org.hnau.commons.app.projector.fractal.utils.size.fPadding
import org.hnau.commons.app.projector.utils.theme.LocalPalettes

@Composable
fun FBase(
    palettes: Palettes,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalPalettes provides palettes,
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