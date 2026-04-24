package org.hnau.commons.app.projector.fractal.utils.color

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import org.hnau.commons.app.model.theme.ThemeBrightness
import org.hnau.commons.app.model.theme.ThemeBrightnessValues
import org.hnau.commons.app.model.theme.fold
import org.hnau.commons.app.model.utils.Hue
import org.hnau.commons.app.projector.dynamiccolor.contrast.Contrast
import org.hnau.commons.app.projector.dynamiccolor.dynamiccolor.ColorSpec
import org.hnau.commons.app.projector.dynamiccolor.dynamiccolor.ColorSpec2026
import org.hnau.commons.app.projector.dynamiccolor.hct.Hct
import org.hnau.commons.app.projector.dynamiccolor.palettes.TonalPalette
import org.hnau.commons.app.projector.fractal.utils.BaseWithDecay
import org.hnau.commons.app.projector.fractal.utils.Distance
import org.hnau.commons.app.projector.fractal.utils.double

class FractalColorsProviderByMainColor(
    private val mainHct: Hct,
    private val themeBrightness: ThemeBrightness,
) : FractalColorsProvider {

    override fun getBackgroundColor(
        distance: Distance,
    ): Color = this
        .getPalette(
            type = PaletteType.Neutral,
        )
        .getHct(
            tone = getBackgroundTone(
                distance = distance,
            )
        )
        .toColor()

    override fun getComponentColors(
        distance: Distance,
        palette: PaletteType,
    ): ComponentValues<Color> {

        val tonalPalette = getPalette(
            type = palette,
        )

        val contrasts: ComponentValues<Double> = contrastForComponent
            .map { it[distance] }

        val containerTone = Contrast.lighterOrDarker(
            tone = getBackgroundTone(
                distance = distance,
            ),
            palette = tonalPalette,
            themeBrightness = themeBrightness,
            ratio = contrasts[ComponentLayer.Container],
        )

        val contentTone = Contrast.lighterOrDarker(
            tone = containerTone,
            palette = tonalPalette,
            themeBrightness = themeBrightness,
            ratio = contrasts[ComponentLayer.Content],
        )

        return ComponentValues(
            container = containerTone,
            content = contentTone
        ).map { tone ->
            tonalPalette
                .getHct(tone)
                .toColor()
        }
    }

    override fun getOutlineComponentColors(
        distance: Distance,
        palette: PaletteType,
    ): OutlineComponentValues<Color> {

        val tonalPalette = getPalette(
            type = palette,
        )

        val backgroundTone = getBackgroundTone(
            distance = distance,
        )

        val contrasts: OutlineComponentValues<Double> = contrastForOutlineComponent
            .map { it[distance] }

        return OutlineComponentValues(
            outline = Contrast.lighterOrDarker(
                tone = backgroundTone,
                palette = tonalPalette,
                themeBrightness = themeBrightness,
                ratio = contrasts[OutlineComponentLayer.Outline],
            ),
            content = Contrast.lighterOrDarker(
                tone = backgroundTone,
                palette = tonalPalette,
                themeBrightness = themeBrightness,
                ratio = contrasts[OutlineComponentLayer.Content],
            )
        ).map { tone ->
            tonalPalette
                .getHct(tone)
                .toColor()
        }
    }

    private val palettesCache = HashMap<PaletteType, TonalPalette>()

    private fun getPalette(
        type: PaletteType,
    ): TonalPalette = palettesCache.getOrPut(
        key = type,
    ) {
        colorSpec.getPalette(
            mainHtc = mainHct,
            type = type,
            config = PaletteConfig.default,
            brightness = themeBrightness,
        )
    }

    private fun getBackgroundTone(
        distance: Distance,
    ): Double {

        val (start, step) = themeBrightness.fold(
            ifLight = { 98.0 to -6.0 },
            ifDark = { 6.0 to 8.0 },
        )

        return (start + (distance.distance * step)).coerceIn(0.0, 100.0)
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

        private fun contrastDecay(
            contrast: Double,
            decay: Double,
        ): BaseWithDecay<Double> = BaseWithDecay.double(
            initial = contrast,
            decay = decay,
            baseline = 1.0,
        )

        private val contrastForComponent: ComponentValues<BaseWithDecay<Double>> = ComponentValues(
            container = contrastDecay(
                contrast = 2.0,
                decay = 0.5,
            ),
            content = contrastDecay(
                contrast = 7.0,
                decay = 0.75,
            ),
        )

        private val contrastForOutlineComponent: OutlineComponentValues<BaseWithDecay<Double>> =
            OutlineComponentValues(
                outline = contrastDecay(
                    contrast = 3.0,
                    decay = 0.6,
                ),
                content = contrastDecay(
                    contrast = 7.0,
                    decay = 0.75,
                ),
            )
    }
}