package org.hnau.commons.app.model.theme

import org.hnau.commons.app.model.color.RGBABytes

fun interface Palette {

    operator fun get(
        tone: Tone,
    ): RGBABytes
}