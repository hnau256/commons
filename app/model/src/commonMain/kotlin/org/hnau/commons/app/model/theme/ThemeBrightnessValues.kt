package org.hnau.commons.app.model.theme

import kotlinx.serialization.Serializable

@Serializable
data class ThemeBrightnessValues<out T>(
    val light: T,
    val dark: T,
) {

    constructor(
        valueBuilder: (ThemeBrightness) -> T,
    ): this(
        light = valueBuilder(ThemeBrightness.Light),
        dark = valueBuilder(ThemeBrightness.Dark),
    )

    operator fun get(
        type: ThemeBrightness,
    ): T = when (type) {
        ThemeBrightness.Light -> light
        ThemeBrightness.Dark -> dark
    }

    inline fun <O> mapFull(
        transform: (ThemeBrightness, T) -> O,
    ): ThemeBrightnessValues<O> = ThemeBrightnessValues(
        light = transform(ThemeBrightness.Light, light),
        dark = transform(ThemeBrightness.Dark, dark),
    )


    inline fun <O> map(
        transform: (T) -> O,
    ): ThemeBrightnessValues<O> = mapFull { _, value ->
        transform(value)
    }
}