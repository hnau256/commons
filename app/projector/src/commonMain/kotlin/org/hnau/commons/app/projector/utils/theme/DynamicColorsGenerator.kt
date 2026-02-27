package org.hnau.commons.app.projector.utils.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import org.hnau.commons.app.model.theme.ThemeBrightness

fun interface DynamicColorsGenerator {

    @Composable
    fun generateDynamicColors(
        brightness: ThemeBrightness,
    ): ColorScheme

    companion object
}