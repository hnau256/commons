package org.hnau.commons.app.model.theme.palette

import org.hnau.commons.app.model.color.dynamic.palettes.TonalPalette
import org.hnau.commons.app.model.theme.ThemeBrightness

data class Palettes(
    val palettes: PaletteTypeValues<TonalPalette>,
    val config: PalettesGenerateConfig,
    val brightness: ThemeBrightness,
) {

    companion object
}