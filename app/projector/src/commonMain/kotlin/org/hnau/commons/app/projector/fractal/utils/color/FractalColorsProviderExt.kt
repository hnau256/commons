package org.hnau.commons.app.projector.fractal.utils.color

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import org.hnau.commons.app.projector.fractal.utils.Distance
import org.hnau.commons.app.projector.fractal.utils.local

@Composable
fun FractalColorsProvider.getBackgroundColor(): Color = getBackgroundColor(
    distance = Distance.local,
)

@Composable
fun FractalColorsProvider.getComponentColors(
    palette: PaletteType,
): ComponentValues<Color> = getComponentColors(
    distance = Distance.local,
    palette = palette,
)

@Composable
fun FractalColorsProvider.getOutlineComponentColors(
    palette: PaletteType,
): OutlineComponentValues<Color> = getOutlineComponentColors(
    distance = Distance.local,
    palette = palette,
)