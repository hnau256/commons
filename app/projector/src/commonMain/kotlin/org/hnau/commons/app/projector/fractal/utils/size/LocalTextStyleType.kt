package org.hnau.commons.app.projector.fractal.utils.size

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf

val LocalTextStyleType: ProvidableCompositionLocal<TextStyleType> = compositionLocalOf { TextStyleType.default }