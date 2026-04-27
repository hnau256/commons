package org.hnau.commons.app.projector.utils

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import org.hnau.commons.app.model.theme.ThemeBrightness
import org.hnau.commons.app.model.theme.Hue
import org.hnau.commons.app.model.color.dynamic.dynamiccolor.Variant
import org.hnau.commons.app.projector.utils.theme.DynamicSchemeConfig
import org.hnau.commons.app.projector.utils.theme.buildColorScheme
import org.hnau.commons.app.projector.utils.theme.themeBrightness

private val DynamicSchemeConfigForHue = DynamicSchemeConfig(
    variant = Variant.TONAL_SPOT,
    contrastLevel = 0.0,
    chroma = 24.0
)

private val schemesCache: MutableMap<Hue, MutableMap<ThemeBrightness, ColorScheme>> =
    kotlin.collections.HashMap()

@Composable
fun SwitchHue(
    hue: Hue,
    content: @Composable () -> Unit,
) {
    val brightness = MaterialTheme.themeBrightness
    val scheme = schemesCache
        .getOrPut(hue) { mutableMapOf() }
        .getOrPut(brightness) {
            buildColorScheme(
                hue = hue,
                config = DynamicSchemeConfigForHue,
                brightness = brightness,
            )
        }
    MaterialTheme(
        colorScheme = scheme,
    ) {
        content()
    }
}