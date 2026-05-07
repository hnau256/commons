package org.hnau.commons.app.projector.fractal.size

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf

val LocalSizeType: ProvidableCompositionLocal<SizeType> = compositionLocalOf { SizeType.default }