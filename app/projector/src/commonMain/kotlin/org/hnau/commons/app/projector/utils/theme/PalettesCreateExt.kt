package org.hnau.commons.app.projector.utils.theme

import org.hnau.commons.app.model.color.dynamic.palettes.TonalPalette
import org.hnau.commons.app.model.theme.ThemeBrightness
import org.hnau.commons.app.model.theme.color.Hue
import org.hnau.commons.app.model.theme.palette.Palettes
import org.hnau.commons.app.model.theme.palette.PalettesGenerateConfig
import org.hnau.commons.app.model.theme.palette.SystemPalettes
import org.hnau.commons.app.model.theme.palette.create

fun Palettes.Companion.create(
    fallbackHue: Hue,
    systemPalettes: SystemPalettes,
    brightness: ThemeBrightness,
    config: PalettesGenerateConfig,
): Palettes = when (systemPalettes) {
    is SystemPalettes.Some -> systemPalettes.palettes
    SystemPalettes.None -> Palettes(
        palettes = TonalPalette.create(
            hue = fallbackHue,
            brightness = brightness,
            config = config,
        ),
        config = config,
        brightness = brightness,
    )
}