package org.hnau.commons.app.projector.utils

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import org.hnau.commons.dynamiccolor.dynamiccolor.Variant
import org.hnau.commons.app.model.theme.ThemeBrightness
import org.hnau.commons.app.model.utils.Hue
import org.hnau.commons.app.projector.utils.theme.DynamicSchemeConfig
import org.hnau.commons.app.projector.utils.theme.buildColorScheme
import org.hnau.commons.app.projector.utils.theme.themeBrightness
import kotlin.collections.getOrPut

private val DynamicSchemeConfigForHue = DynamicSchemeConfig(
    variant = Variant.FIDELITY,
    contrastLevel = 0.0,
    //chroma = 100.0,
    /*tone = ThemeBrightnessValues(
        light = 50.0,
        dark = 50.0,
    )*/
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
        .getOrPut(hue) { kotlin.collections.HashMap() }
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
        CompositionLocalProvider(
            LocalContentColor provides MaterialTheme.colorScheme.primaryContainer,
        ) {
            content()
        }
    }
}