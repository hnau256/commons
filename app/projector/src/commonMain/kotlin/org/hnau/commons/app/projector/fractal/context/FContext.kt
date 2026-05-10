package org.hnau.commons.app.projector.fractal.context

import org.hnau.commons.app.model.theme.color.Tone
import org.hnau.commons.app.model.theme.palette.Palettes
import org.hnau.commons.app.projector.fractal.utils.Distance
import org.hnau.commons.app.projector.fractal.utils.Mood
import org.hnau.commons.app.projector.fractal.utils.Saturation

data class FContext(
    val palettes: Palettes,
    val distance: Distance = Distance.zero,
    val mood: Mood = Mood.default,
    val saturation: Saturation = Saturation.default,
    val customContainerTone: Tone? = null,
)