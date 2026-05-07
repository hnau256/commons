package org.hnau.commons.app.projector.fractal.context

import org.hnau.commons.app.model.theme.color.Tone
import org.hnau.commons.app.model.theme.palette.PaletteType
import org.hnau.commons.app.model.theme.palette.Palettes
import org.hnau.commons.app.projector.fractal.utils.Distance

data class FContext(
    val distance: Distance,
    val palettes: Palettes,
    val palette: PaletteType = PaletteType.Neutral,
    val customTone: Tone? = null,
)