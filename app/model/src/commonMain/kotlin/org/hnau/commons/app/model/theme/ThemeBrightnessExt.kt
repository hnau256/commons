package org.hnau.commons.app.model.theme

inline fun <R> ThemeBrightness.fold(
    ifLight: () -> R,
    ifDark: () -> R,
): R = when (this) {
    ThemeBrightness.Light -> ifLight()
    ThemeBrightness.Dark -> ifDark()
}