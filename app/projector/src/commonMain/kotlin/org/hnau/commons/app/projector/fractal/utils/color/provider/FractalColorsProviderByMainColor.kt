package org.hnau.commons.app.projector.fractal.utils.color.provider

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import org.hnau.commons.app.model.theme.ThemeBrightness
import org.hnau.commons.app.model.theme.ThemeBrightnessValues
import org.hnau.commons.app.model.theme.fold
import org.hnau.commons.app.model.theme.Hue
import org.hnau.commons.app.model.color.dynamic.dynamiccolor.ColorSpec
import org.hnau.commons.app.model.color.dynamic.dynamiccolor.ColorSpec2026
import org.hnau.commons.app.model.color.dynamic.hct.Hct
import org.hnau.commons.app.model.color.dynamic.palettes.TonalPalette
import org.hnau.commons.app.projector.fractal.utils.BaseWithDecay
import org.hnau.commons.app.projector.fractal.utils.Distance
import org.hnau.commons.app.projector.fractal.utils.color.PaletteConfig
import org.hnau.commons.app.model.theme.PaletteType
import org.hnau.commons.app.projector.fractal.utils.color.contrast.Contrast
import org.hnau.commons.app.projector.fractal.utils.color.contrast.findContrasted
import org.hnau.commons.app.projector.fractal.utils.color.getPalette
import org.hnau.commons.app.projector.fractal.utils.color.toColor
import org.hnau.commons.app.model.theme.Tone
import org.hnau.commons.app.projector.fractal.utils.color.tone.getHct

class FractalColorsProviderByMainColor(
    private val mainHct: Hct,
    private val themeBrightness: ThemeBrightness,
) : FractalColorsProvider {

    override fun getBackgroundTone(
        distance: Distance,
    ): Tone {

        val (start, step) = themeBrightness.fold(
            ifLight = { Tone.create(98.0) to Tone.create(-6.0) },
            ifDark = { Tone.create(6.0) to Tone.create(8.0) },
        )

        return start + (step * distance.distance)
    }

    override fun getForegroundTone(
        backgroundTone: Tone,
        distance: Distance,
        contrast: BaseWithDecay<Contrast>,
        palette: PaletteType,
    ): Tone = backgroundTone.findContrasted(
        palette = getPalette(
            type = palette,
        ),
        themeBrightness = themeBrightness,
        ratio = contrast[distance],
    )

    override fun getColor(
        tone: Tone,
        palette: PaletteType,
    ): Color = getPalette(
        type = palette,
    )
        .getHct(
            tone = tone,
        )
        .toColor()

    private val palettesCache = HashMap<PaletteType, TonalPalette>()

    private fun getPalette(
        type: PaletteType,
    ): TonalPalette = palettesCache.getOrPut(
        key = type,
    ) {
        colorSpec.getPalette(
            mainHtc = mainHct,
            type = type,
            config = PaletteConfig.Companion.default,
            brightness = themeBrightness,
        )
    }

    companion object {

        fun createFromColor(
            mainColor: Color,
            themeBrightness: ThemeBrightness,
        ): FractalColorsProviderByMainColor = FractalColorsProviderByMainColor(
            mainHct = Hct.fromInt(mainColor.toArgb()),
            themeBrightness = themeBrightness,
        )

        fun fromHue(
            hue: Hue,
            themeBrightness: ThemeBrightness,
        ): FractalColorsProviderByMainColor = FractalColorsProviderByMainColor(
            mainHct = Hct.from(
                /* hue = */ hue.degrees.toDouble(),
                /* chroma = */ chroma,
                /* tone = */ tone[themeBrightness],
            ),
            themeBrightness = themeBrightness,
        )

        private val chroma: Double = 48.0
        private val tone: ThemeBrightnessValues<Double> = ThemeBrightnessValues(
            light = 40.0,
            dark = 80.0,
        )

        private val colorSpec: ColorSpec = ColorSpec2026()
    }
}