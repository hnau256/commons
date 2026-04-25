package org.hnau.commons.app.projector.fractal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import org.hnau.commons.app.projector.fractal.utils.OffsetDistance
import org.hnau.commons.app.projector.fractal.utils.SwitchPalette
import org.hnau.commons.app.projector.fractal.utils.color.PaletteType
import org.hnau.commons.app.projector.fractal.utils.color.localBackground
import org.hnau.commons.app.projector.fractal.utils.color.tone.SwitchBackgroundTone
import org.hnau.commons.app.projector.fractal.utils.size.FUnits
import org.hnau.commons.app.projector.fractal.utils.size.fPadding

@Composable
fun FPanel(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    SwitchPalette(
        newPalette = PaletteType.Neutral,
    ) {
        OffsetDistance(
            offset = 1,
        ) {
            SwitchBackgroundTone {
                val units = FUnits.local
                Box(
                    modifier = modifier
                        .background(
                            color = Color.localBackground,
                            shape = units.shape,
                        )
                        .fPadding(),
                ) {
                    content()
                }
            }
        }
    }
}