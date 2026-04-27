package org.hnau.commons.app.model.theme.palette

import org.hnau.commons.app.model.color.dynamic.hct.Hct
import org.hnau.commons.app.model.theme.color.Tone

fun interface Palette {

    operator fun get(
        tone: Tone,
    ): Hct

    companion object
}