package org.hnau.commons.app.model.theme

import org.hnau.commons.app.model.color.dynamic.hct.Hct

fun interface Palette {

    operator fun get(
        tone: Tone,
    ): Hct
}