package org.hnau.commons.app.projector.app

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import kotlinx.coroutines.CoroutineScope
import org.hnau.commons.app.model.app.AppModel
import org.hnau.commons.app.model.theme.ThemeBrightness
import org.hnau.commons.app.model.theme.color.Hue
import org.hnau.commons.app.model.theme.palette.Palettes
import org.hnau.commons.app.model.theme.palette.PalettesGenerateConfig
import org.hnau.commons.app.model.theme.palette.SystemPalettes
import org.hnau.commons.app.projector.fractal.FBase
import org.hnau.commons.app.projector.fractal.utils.color.provider.FractalColorsProvider
import org.hnau.commons.app.projector.fractal.utils.color.provider.FractalColorsProviderByPalettes
import org.hnau.commons.app.projector.utils.theme.createFromSystemOrFallback
import org.hnau.commons.app.projector.utils.theme.system
import org.hnau.commons.app.projector.utils.theme.toColorScheme

class AppProjector<M, S, P>(
    scope: CoroutineScope,
    private val model: AppModel<M, S>,
    private val systemPalettes: (ThemeBrightness) -> SystemPalettes,
    private val fallbackHue: Hue,
    private val palettesGenerateConfig: PalettesGenerateConfig = PalettesGenerateConfig.default,
    createProjector: (CoroutineScope, M) -> P,
    private val content: @Composable (P, PaddingValues) -> Unit,
) {

    private val projector = createProjector(
        scope,
        model.model,
    )

    @Composable
    fun Content(
        contentPadding: PaddingValues,
    ) {

        val brightness: ThemeBrightness = ThemeBrightness.system

        //TODO remember
        val palettes: Palettes = Palettes.createFromSystemOrFallback(
            fallbackHue = fallbackHue,
            systemPalettes = systemPalettes(brightness),
            brightness = brightness,
            config = palettesGenerateConfig,
        )

        //TODO remember
        val fractalColorsProvider: FractalColorsProvider = FractalColorsProviderByPalettes(
            palettes = palettes,
        )

        MaterialTheme(
            colorScheme = palettes.toColorScheme(), //TODO remember
        ) {
            FBase(
                fractalColorsProvider = fractalColorsProvider,
            ) {
                CompositionLocalProvider(
                    LocalContentColor provides MaterialTheme.colorScheme.onBackground,
                ) {
                    content(projector, contentPadding)
                }
            }
        }
    }
}