package org.hnau.commons.app.projector.utils

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import org.hnau.commons.app.model.theme.color.Chroma
import org.hnau.commons.app.model.theme.color.Hue
import org.hnau.commons.app.model.theme.palette.Palettes
import org.hnau.commons.app.model.theme.palette.PalettesGenerateConfig
import org.hnau.commons.app.projector.fractal.utils.color.provider.FractalColorsProvider
import org.hnau.commons.app.projector.fractal.utils.color.provider.FractalColorsProviderByPalettes
import org.hnau.commons.app.projector.fractal.utils.color.provider.LocalFractalColorsProvider
import org.hnau.commons.app.projector.utils.theme.create
import org.hnau.commons.app.projector.utils.theme.themeBrightness
import org.hnau.commons.app.projector.utils.theme.toColorScheme

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

    //TODO remember
    val palettes: Palettes = Palettes.create(
        hue = hue,
        brightness = brightness,
        config = DynamicSchemeConfigForHue,
    )

    //TODO remember
    val fractalColorsProvider: FractalColorsProvider = FractalColorsProviderByPalettes(
        palettes = palettes,
    )
    MaterialTheme(
        colorScheme = palettes.toColorScheme(), //TODO remember
    ) {
        CompositionLocalProvider(
            LocalFractalColorsProvider provides fractalColorsProvider,
        ) {
            content()
        }
    }
}