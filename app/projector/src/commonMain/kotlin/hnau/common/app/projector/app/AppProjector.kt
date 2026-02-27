package hnau.common.app.projector.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import hnau.common.kotlin.ifNull
import hnau.common.kotlin.ifTrue
import hnau.common.app.model.theme.ThemeBrightness
import hnau.common.app.model.app.AppModel
import hnau.common.app.projector.utils.theme.DynamicColorsGenerator
import hnau.common.app.projector.utils.theme.buildColorScheme
import hnau.common.app.projector.utils.theme.provideDynamicColorsGenerator
import hnau.common.app.projector.utils.theme.system
import hnau.common.app.projector.utils.theme.DynamicSchemeConfig
import kotlinx.coroutines.CoroutineScope

class AppProjector<M, S, P>(
    scope: CoroutineScope,
    private val model: AppModel<M, S>,
    private val schemeConfig: DynamicSchemeConfig = DynamicSchemeConfig.default,
    createProjector: (CoroutineScope, M) -> P,
    private val content: @Composable (P) -> Unit,
) {

    private val projector = createProjector(
        scope,
        model.model,
    )

    private val dynamicColorsGenerator: DynamicColorsGenerator? =
        provideDynamicColorsGenerator()

    @Composable
    fun Content() {

        val brightness: ThemeBrightness = model
            .appContext
            .brightness
            .value
            .collectAsState()
            .value
            ?: ThemeBrightness.system

        val colorScheme = model
            .appContext
            .tryUseSystemHue
            .value
            .collectAsState()
            .value
            .ifTrue { dynamicColorsGenerator }
            ?.generateDynamicColors(brightness)
            .ifNull {
                val hue = model
                    .appContext
                    .fallbackHue
                    .value
                    .collectAsState()
                    .value
                buildColorScheme(
                    hue = hue,
                    config = schemeConfig,
                    brightness = brightness,
                )
            }

        MaterialTheme(
            colorScheme = colorScheme,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
            ) {
                CompositionLocalProvider(
                    LocalContentColor provides MaterialTheme.colorScheme.onBackground,
                ) {
                    content(projector)
                }
            }
        }
    }
}