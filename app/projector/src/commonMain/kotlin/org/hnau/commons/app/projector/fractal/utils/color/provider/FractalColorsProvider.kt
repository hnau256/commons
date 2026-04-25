package org.hnau.commons.app.projector.fractal.utils.color.provider

import androidx.compose.ui.graphics.Color
import org.hnau.commons.app.projector.fractal.utils.BaseWithDecay
import org.hnau.commons.app.projector.fractal.utils.Distance
import org.hnau.commons.app.projector.fractal.utils.color.PaletteType
import org.hnau.commons.app.projector.fractal.utils.color.contrast.Contrast
import org.hnau.commons.app.projector.fractal.utils.color.tone.Tone

interface FractalColorsProvider {

    fun getBackgroundTone(
        distance: Distance,
    ): Tone

    fun getForegroundTone(
        backgroundTone: Tone,
        distance: Distance,
        contrast: BaseWithDecay<Contrast>,
        palette: PaletteType,
    ): Tone

    fun getColor(
        tone: Tone,
        palette: PaletteType,
    ): Color

    companion object
}