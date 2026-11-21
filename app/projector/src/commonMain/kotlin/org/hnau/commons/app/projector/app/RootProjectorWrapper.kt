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
import org.hnau.commons.app.model.app.RootModelWrapper
import org.hnau.commons.app.model.theme.ThemeBrightness
import org.hnau.commons.app.model.theme.palette.SystemPalettes
import org.hnau.commons.app.projector.fractal.context.FContext
import org.hnau.commons.app.projector.fractal.context.LocalFContext
import org.hnau.commons.app.projector.uikit.backbutton.BackButtonHost
import org.hnau.commons.app.projector.utils.theme.PalettesWithColorScheme
import org.hnau.commons.app.projector.utils.theme.system

internal class RootProjectorWrapper<M, P>(
    scope: CoroutineScope,
    private val model: RootModelWrapper<*, M>,
    private val root: RootProjectorFactory<M, P>,
    private val createSystemPalettes: (ThemeBrightness) -> SystemPalettes,
) {

    private val rootProjector: P = root.createRootProjector(scope, model.model)

    @Composable
    fun Content(
        contentPadding: PaddingValues,
    ) {

        val brightness: ThemeBrightness = ThemeBrightness.system

        val palettesWithColorScheme = PalettesWithColorScheme.createCached(
            hue = root.fallbackHue,
            systemPalettes = remember(createSystemPalettes, brightness) {
                createSystemPalettes(brightness)
            },
            brightness = brightness,
            config = root.palettesGenerateConfig,
        )

        MaterialTheme(
            colorScheme = palettesWithColorScheme.colorScheme,
        ) {
            CompositionLocalProvider(
                LocalContentColor provides MaterialTheme.colorScheme.onBackground,
                LocalFContext provides FContext.createBase(
                    palettes = palettesWithColorScheme.palettes,
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background),
                ) {
                    BackButtonHost(
                        contentPadding = contentPadding,
                        goBackHandler = model.goBackHandler,
                    ) { contentPadding ->
                        root.content(
                            rootProjector,
                            contentPadding,
                        )
                    }
                }
            }
        }
    }
}