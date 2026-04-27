package org.hnau.commons.app.projector.fractal.utils.color.provider

import androidx.compose.ui.graphics.Color
import org.hnau.commons.app.model.theme.color.Contrast
import org.hnau.commons.app.model.theme.color.Tone
import org.hnau.commons.app.model.theme.fold
import org.hnau.commons.app.model.theme.palette.PaletteType
import org.hnau.commons.app.model.theme.palette.Palettes
import org.hnau.commons.app.projector.fractal.utils.BaseWithDecay
import org.hnau.commons.app.projector.fractal.utils.Distance
import org.hnau.commons.app.projector.fractal.utils.color.contrast.findContrasted
import org.hnau.commons.app.projector.fractal.utils.color.toColor
import org.hnau.commons.app.projector.fractal.utils.color.tone.getHct

class FractalColorsProviderByPalettes(
    private val palettes: Palettes,
) : FractalColorsProvider {

    private val themeBrightness
        get() = palettes.brightness

    override fun getBackgroundTone(
        distance: Distance,
    ): Tone {

        val (start, step) = themeBrightness.fold(
            ifLight = { Tone.create(98) to Tone.create(-6) },
            ifDark = { Tone.create(6) to Tone.create(8) },
        )

        return start + (step * distance.distance)
    }

    override fun getForegroundTone(
        backgroundTone: Tone,
        distance: Distance,
        contrast: BaseWithDecay<Contrast>,
        palette: PaletteType,
    ): Tone = backgroundTone.findContrasted(
        palette = palettes.palettes[palette],
        themeBrightness = themeBrightness,
        ratio = contrast[distance],
    )

    override fun getColor(
        tone: Tone,
        palette: PaletteType,
    ): Color = palettes.palettes[palette]
        .getHct(
            tone = tone,
        )
        .toColor()
}