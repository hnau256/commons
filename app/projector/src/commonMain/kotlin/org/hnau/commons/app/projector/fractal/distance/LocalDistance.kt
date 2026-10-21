package org.hnau.commons.app.projector.fractal.distance

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf

val LocalDistance: ProvidableCompositionLocal<Distance> =
    compositionLocalOf { Distance.zero }

@Composable
fun DistanceOffset(
    offset: Int = 1,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        value = LocalDistance provides LocalDistance.current.plus(offset),
        content = content,
    )
}