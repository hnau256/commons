package org.hnau.commons.app.projector.utils.theme

import org.hnau.commons.app.model.color.dynamic.palettes.TonalPalette
import org.hnau.commons.app.model.theme.ThemeBrightness
import org.hnau.commons.app.model.theme.color.Hue
import org.hnau.commons.app.model.theme.palette.Palettes
import org.hnau.commons.app.model.theme.palette.PalettesGenerateConfig
import org.hnau.commons.app.model.theme.palette.SystemPalettes
import org.hnau.commons.app.model.theme.palette.createAll

fun Palettes.Companion.createCached(
    fallbackHue: Hue,
    systemPalettes: SystemPalettes,
    brightness: ThemeBrightness,
    config: PalettesGenerateConfig = PalettesGenerateConfig.default,
): Palettes = when (systemPalettes) {
    is SystemPalettes.Some -> systemPalettes.palettes
    SystemPalettes.None -> palettesCache
        .getOrPut(fallbackHue) { HashMap() }
        .getOrPut(brightness) { HashMap() }
        .getOrPut(config) {
            Palettes(
                palettes = TonalPalette.createAll(
                    hue = fallbackHue,
                    brightness = brightness,
                    config = config,
                ),
                config = config,
                brightness = brightness,
            )
        }
}

private val palettesCache: MutableMap<Hue, MutableMap<ThemeBrightness, MutableMap<PalettesGenerateConfig, Palettes>>> =
    HashMap()
