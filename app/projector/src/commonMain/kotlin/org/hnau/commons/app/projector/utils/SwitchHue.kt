package org.hnau.commons.app.projector.utils

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import org.hnau.commons.app.model.theme.color.Chroma
import org.hnau.commons.app.model.theme.color.Hue
import org.hnau.commons.app.model.theme.palette.PalettesGenerateConfig
import org.hnau.commons.app.model.theme.palette.SystemPalettes
import org.hnau.commons.app.projector.utils.theme.LocalPalettes
import org.hnau.commons.app.projector.utils.theme.PalettesWithColorScheme
import org.hnau.commons.app.projector.utils.theme.themeBrightness

private val DynamicSchemeConfigForHue: PalettesGenerateConfig = PalettesGenerateConfig
    .default
    .copy(
        chroma = Chroma.create(24)
    )

@Composable
fun SwitchHue(
    hue: Hue,
    content: @Composable () -> Unit,
) {
    val brightness = MaterialTheme.themeBrightness

    val palettesWithColorScheme = PalettesWithColorScheme.createCached(
        hue = hue,
        systemPalettes = SystemPalettes.None,
        brightness = brightness,
        config = DynamicSchemeConfigForHue,
    )

    MaterialTheme(
        colorScheme = palettesWithColorScheme.colorScheme,
    ) {
        CompositionLocalProvider(
            LocalPalettes provides palettesWithColorScheme.palettes,
        ) {
            content()
        }
    }
}