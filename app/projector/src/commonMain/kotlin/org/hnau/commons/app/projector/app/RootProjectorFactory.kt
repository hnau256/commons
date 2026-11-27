package org.hnau.commons.app.projector.app

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import kotlinx.coroutines.CoroutineScope
import org.hnau.commons.app.model.theme.color.Hue
import org.hnau.commons.app.model.theme.palette.PalettesGenerateConfig

data class RootProjectorFactory<M, P>(
    val fallbackHue: Hue,
    val palettesGenerateConfig: PalettesGenerateConfig = PalettesGenerateConfig.default,
    val createRootProjector: (CoroutineScope, M, AppUIContext) -> P,
    val content: @Composable (P, PaddingValues) -> Unit,
)