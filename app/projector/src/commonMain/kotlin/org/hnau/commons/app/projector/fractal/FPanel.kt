package org.hnau.commons.app.projector.fractal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import org.hnau.commons.app.model.theme.palette.PaletteType
import org.hnau.commons.app.projector.fractal.utils.LocalDistance
import org.hnau.commons.app.projector.fractal.utils.OffsetDistance
import org.hnau.commons.app.projector.fractal.utils.SwitchPalette
import org.hnau.commons.app.projector.fractal.utils.color.localBackground
import org.hnau.commons.app.projector.fractal.utils.color.tone.SwitchBackgroundTone
import org.hnau.commons.app.projector.fractal.utils.size.fPadding
import org.hnau.commons.app.projector.fractal.utils.size.units

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
                val units = LocalDistance.current.units
                Box(
                    modifier = modifier
                        .background(
                            color = Color.localBackground,
                            shape = units.shape,
                        )
                        .fPadding(),
                    propagateMinConstraints = true,
                ) {
                    content()
                }
            }
        }
    }
}