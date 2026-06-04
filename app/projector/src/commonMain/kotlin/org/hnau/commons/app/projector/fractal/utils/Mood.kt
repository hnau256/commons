package org.hnau.commons.app.projector.fractal.utils

import org.hnau.commons.gen.fold.annotations.Fold

@Fold
enum class Mood {
    Primary, Secondary, Tertiary, Error;

    companion object {

        val default: Mood
            get() = Primary
    }
}