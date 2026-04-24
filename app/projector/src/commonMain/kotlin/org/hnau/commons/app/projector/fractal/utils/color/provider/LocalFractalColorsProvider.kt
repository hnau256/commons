package org.hnau.commons.app.projector.fractal.utils.color.provider

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf

val LocalFractalColorsProvider: ProvidableCompositionLocal<FractalColorsProvider> =
    compositionLocalOf { error("FractalColorsProvider isn't present") }

val FractalColorsProvider.Companion.local: FractalColorsProvider
    @Composable
    get() = LocalFractalColorsProvider.current