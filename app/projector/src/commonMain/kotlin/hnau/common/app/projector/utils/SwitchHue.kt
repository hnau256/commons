package hnau.common.app.projector.utils

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import dynamiccolor.Variant
import hnau.common.app.model.theme.ThemeBrightness
import hnau.common.app.model.utils.Hue
import hnau.common.app.projector.utils.theme.DynamicSchemeConfig
import hnau.common.app.projector.utils.theme.buildColorScheme
import hnau.common.app.projector.utils.theme.themeBrightness
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