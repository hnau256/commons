package org.hnau.commons.app.model.color

import org.hnau.commons.app.model.color.gradient.Gradient
import org.hnau.commons.app.model.color.gradient.create

private val hueGradientLocal: Gradient<RGBABytes> = Gradient.create(
    RGBABytes.Red,
    RGBABytes.Yellow,
    RGBABytes.Green,
    RGBABytes.Cyan,
    RGBABytes.Blue,
    RGBABytes.Magenta,
    RGBABytes.Red,
)

val RGBABytes.Companion.hueGradient: Gradient<RGBABytes>
    get() = hueGradientLocal