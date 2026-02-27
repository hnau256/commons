package hnau.common.app.projector.utils.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.luminance
import hnau.common.app.model.theme.ThemeBrightness

val ThemeBrightness.Companion.system: ThemeBrightness
    @Composable
    get() = when (isSystemInDarkTheme()) {
        true -> ThemeBrightness.Dark
        false -> ThemeBrightness.Light
    }

val MaterialTheme.themeBrightness: ThemeBrightness
    @Composable
    get() = when {
        colorScheme.background.luminance() > 0.5 -> ThemeBrightness.Light
        else -> ThemeBrightness.Dark
    }