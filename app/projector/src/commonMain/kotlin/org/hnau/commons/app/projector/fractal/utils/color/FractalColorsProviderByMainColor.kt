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

    private data class ComponentInfo(
        val palette: TonalPalette,
        val distance: Distance,
    )

    private fun getComponentInfo(
        distanceWithImportance: DistanceWithImportance,
    ): ComponentInfo {
        val distance = distanceWithImportance.distanceByImportance
        return ComponentInfo(
            distance = distance,
            palette = getPalette(
                type = when (distance.distance) {
                    0 -> PaletteType.Primary
                    1 -> PaletteType.Secondary
                    2 -> PaletteType.Tertiarty
                    else -> PaletteType.Neutral
                }
            )
        )
    }

    override fun getComponentColors(
        distanceWithImportance: DistanceWithImportance,
    ): ComponentValues<Color> {

        val (palette, distance) = getComponentInfo(
            distanceWithImportance = distanceWithImportance,
        )

        val backgroundTone = getBackgroundTone(
            distance = distanceWithImportance.distance,
        )

        val contrasts: ComponentValues<Double> = contrastBaseAndDecayForComponent
            .map { contrastBaseAndDecay -> contrastBaseAndDecay[distance] + 1.0 }

        val containerTone = Contrast.lighterOrDarker(
            tone = backgroundTone,
            palette = palette,
            themeBrightness = themeBrightness,
            ratio = contrasts[ComponentLayer.Container],
        )

        val contentTone = Contrast.lighterOrDarker(
            tone = containerTone,
            palette = palette,
            themeBrightness = themeBrightness,
            ratio = contrasts[ComponentLayer.Content],
        )

        return ComponentValues(
            container = containerTone,
            content = contentTone
        ).map { tone ->
            palette
                .getHct(tone)
                .toColor()
        }
    }

    override fun getOutlineComponentColors(
        distanceWithImportance: DistanceWithImportance,
    ): OutlineComponentValues<Color> {
        val (palette, distance) = getComponentInfo(
            distanceWithImportance = distanceWithImportance,
        )

        val backgroundTone = getBackgroundTone(
            distance = distanceWithImportance.distance,
        )

        val contrasts: OutlineComponentValues<Double> = contrastBaseAndDecayForOutlineComponent
            .map { contrastBaseAndDecay -> contrastBaseAndDecay[distance] + 1.0 }

        return OutlineComponentValues(
            outline = Contrast.lighterOrDarker(
                tone = backgroundTone,
                palette = palette,
                themeBrightness = themeBrightness,
                ratio = contrasts[OutlineComponentLayer.Outline],
            ),
            content = Contrast.lighterOrDarker(
                tone = backgroundTone,
                palette = palette,
                themeBrightness = themeBrightness,
                ratio = contrasts[OutlineComponentLayer.Content],
            )
        ).map { tone ->
            palette
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
            paletteType = type,
            config = PaletteConfig.default,
            brightness = themeBrightness,
        )
    }

    private fun getBackgroundTone(
        distance: Distance,
    ): Double {

        val (start, step) = themeBrightness.fold(
            ifLight = { 98.0 to -4.0 },
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

        private val contrastBaseAndDecayForComponent: ComponentValues<BaseWithDecay<Double>> =
            ComponentValues(
                container = BaseWithDecay.double(
                    base = 1.0,
                    decay = 0.8,
                ),
                content = BaseWithDecay.double(
                    base = 6.0,
                    decay = 0.9,
                ),
            )

        private val contrastBaseAndDecayForOutlineComponent: OutlineComponentValues<BaseWithDecay<Double>> =
            OutlineComponentValues(
                outline = BaseWithDecay.double(
                    base = 2.0,
                    decay = 0.8,
                ),
                content = BaseWithDecay.double(
                    base = 6.0,
                    decay = 0.9,
                ),
            )
    }
}