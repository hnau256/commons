package org.hnau.commons.app.projector.fractal.utils.color

import androidx.compose.ui.graphics.Color
import org.hnau.commons.app.projector.fractal.utils.Distance

interface FractalColorsProvider {

    fun getBackgroundColor(
        distance: Distance,
    ): Color

    fun getComponentColors(
        distance: Distance,
        palette: PaletteType,
    ): ComponentValues<Color>

    fun getOutlineComponentColors(
        distance: Distance,
        palette: PaletteType,
    ): OutlineComponentValues<Color>

    companion object
}