package org.hnau.commons.app.model.theme

import org.hnau.commons.gen.enumvalues.annotations.EnumValues
import org.hnau.commons.gen.fold.annotations.Fold

@Fold
@EnumValues
enum class ThemeBrightness {
    Light,
    Dark,
    ;

    companion object
}

val ThemeBrightness.isDark: Boolean
    get() = fold(
        ifLight = { false },
        ifDark = { true }
    )