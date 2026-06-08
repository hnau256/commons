package org.hnau.commons.app.projector.fractal.padding

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf

val LocalContentPadding: ProvidableCompositionLocal<PaddingValues> =
    compositionLocalOf { PaddingValues.Zero }