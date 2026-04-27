package org.hnau.commons.app.model.theme

import org.hnau.commons.gen.enumvalues.annotations.EnumValues

@EnumValues
enum class ThemeBrightness {
    Light,
    Dark,
    ;

    companion object
}

inline fun <R> ThemeBrightness.fold(
    ifLight: () -> R,
    ifDark: () -> R,
): R = when (this) {
    ThemeBrightness.Light -> ifLight()
    ThemeBrightness.Dark -> ifDark()
}