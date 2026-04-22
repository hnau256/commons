package org.hnau.commons.app.projector.fractal.utils.color

import androidx.compose.ui.graphics.Color
import org.hnau.commons.app.projector.fractal.utils.Distance

interface FractalColorsProvider {

    fun getBackgroundColor(
        distance: Distance,
    ): Color

    fun getComponentColors(
        distanceWithImportance: DistanceWithImportance,
    ): ComponentValues<Color>

    fun getOutlineComponentColors(
        distanceWithImportance: DistanceWithImportance,
    ): OutlineComponentValues<Color>

    companion object
}