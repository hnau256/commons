package org.hnau.commons.app.projector.fractal.context

import org.hnau.commons.app.model.theme.color.Tone
import org.hnau.commons.app.model.theme.fold
import org.hnau.commons.app.model.theme.palette.Palettes
import org.hnau.commons.app.projector.fractal.utils.Distance
import org.hnau.commons.app.projector.fractal.utils.Mood
import org.hnau.commons.app.projector.fractal.utils.Saturation

data class FContext(
    val palettes: Palettes,
    val distance: Distance,
    val mood: Mood,
    val saturation: Saturation,
    val tone: Tone,
) {

    companion object {

        fun createBase(
            palettes: Palettes,
        ): FContext = FContext(
            palettes = palettes,
            distance = Distance.zero,
            mood = Mood.default,
            saturation = Saturation.default,
            tone = palettes.brightness.fold(
                ifLight = { Tone.create(92.0) },
                ifDark = { Tone.create(6.0) },
            )
        )
    }
}