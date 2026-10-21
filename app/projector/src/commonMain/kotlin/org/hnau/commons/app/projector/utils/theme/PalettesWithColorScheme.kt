package org.hnau.commons.app.projector.utils.theme

import androidx.compose.material3.ColorScheme
import org.hnau.commons.app.model.theme.ThemeBrightness
import org.hnau.commons.app.model.theme.color.Hue
import org.hnau.commons.app.model.theme.palette.Palettes
import org.hnau.commons.app.model.theme.palette.PalettesGenerateConfig
import org.hnau.commons.app.model.theme.palette.SystemPalettes

data class PalettesWithColorScheme(
    val palettes: Palettes,
    val colorScheme: ColorScheme,
) {

    companion object {

        fun createCached(
            hue: Hue,
            brightness: ThemeBrightness,
            config: PalettesGenerateConfig,
            systemPalettes: SystemPalettes,
        ): PalettesWithColorScheme {
            val palettes = Palettes.createCached(
                fallbackHue = hue,
                brightness = brightness,
                config = config,
                systemPalettes = systemPalettes,
            )
            return palettesWithColorSchemeCache.getOrPut(
                key = palettes,
            ) {
                PalettesWithColorScheme(
                    palettes = palettes,
                    colorScheme = palettes.toColorScheme(),
                )
            }
        }

        private val palettesWithColorSchemeCache: MutableMap<Palettes, PalettesWithColorScheme> =
            HashMap()
    }
}