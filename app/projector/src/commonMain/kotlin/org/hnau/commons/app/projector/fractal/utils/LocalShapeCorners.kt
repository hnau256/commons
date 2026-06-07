package org.hnau.commons.app.projector.fractal.utils

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf

val LocalShapeCorners: ProvidableCompositionLocal<ShapeCorners.Provider> =
    compositionLocalOf { ShapeCorners.Provider.opened }