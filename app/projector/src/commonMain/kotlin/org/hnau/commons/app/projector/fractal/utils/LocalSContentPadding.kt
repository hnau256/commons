package org.hnau.commons.app.projector.fractal.utils

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf

val LocalSContentPadding: ProvidableCompositionLocal<PaddingValues> =
    compositionLocalOf { error("ContentPadding is not provided") }