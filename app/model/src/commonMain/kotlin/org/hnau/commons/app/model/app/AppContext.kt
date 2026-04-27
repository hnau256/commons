package org.hnau.commons.app.model.app

import org.hnau.commons.app.model.file.File
import org.hnau.commons.app.model.preferences.Preference
import org.hnau.commons.app.model.theme.ThemeBrightness
import org.hnau.commons.app.model.theme.color.Hue

data class AppContext(
    val brightness: Preference<ThemeBrightness?>,
    val tryUseSystemHue: Preference<Boolean>,
    val fallbackHue: Preference<Hue>,
    val filesDir: File,
)
