package org.hnau.commons.app.projector.fractal.context

import org.hnau.commons.app.model.theme.color.Tone
import org.hnau.commons.app.model.theme.fold
import org.hnau.commons.app.model.theme.palette.Palettes
import org.hnau.commons.app.projector.fractal.utils.Mood

data class FContext(
    val palettes: Palettes,
    val mood: Mood,
    val tone: Tone,
) {

    companion object {

        fun createBase(
            palettes: Palettes,
        ): FContext = FContext(
            palettes = palettes,
            mood = Mood.default,
            tone = palettes.brightness.fold(
                ifLight = { Tone.create(92.0) },
                ifDark = { Tone.create(6.0) },
            )
        )
    }
}