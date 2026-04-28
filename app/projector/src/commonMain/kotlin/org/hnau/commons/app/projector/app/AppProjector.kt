package org.hnau.commons.app.projector.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import kotlinx.coroutines.CoroutineScope
import org.hnau.commons.app.model.app.AppModel
import org.hnau.commons.app.model.theme.ThemeBrightness
import org.hnau.commons.app.model.theme.color.Hue
import org.hnau.commons.app.model.theme.palette.PalettesGenerateConfig
import org.hnau.commons.app.model.theme.palette.SystemPalettes
import org.hnau.commons.app.projector.utils.theme.LocalPalettes
import org.hnau.commons.app.projector.utils.theme.PalettesWithColorScheme
import org.hnau.commons.app.projector.utils.theme.system

class AppProjector<M, S, P>(
    scope: CoroutineScope,
    private val model: AppModel<M, S>,
    private val createSystemPalettes: (ThemeBrightness) -> SystemPalettes,
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

        val palettesWithColorScheme = PalettesWithColorScheme.createCached(
            hue = fallbackHue,
            systemPalettes = remember(createSystemPalettes, brightness) {
                createSystemPalettes(brightness)
            },
            brightness = brightness,
            config = palettesGenerateConfig,
        )

        MaterialTheme(
            colorScheme = palettesWithColorScheme.colorScheme,
        ) {
            CompositionLocalProvider(
                LocalContentColor provides MaterialTheme.colorScheme.onBackground,
                LocalPalettes provides palettesWithColorScheme.palettes,
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background),
                ) {
                    content(projector, contentPadding)
                }
            }
        }
    }
}