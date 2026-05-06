package org.hnau.commons.app.projector.fractal.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf

val LocalDistance: ProvidableCompositionLocal<Distance> = compositionLocalOf { Distance.zero }

@Composable
fun OffsetDistance(
    offset: Int,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        value = LocalDistance provides LocalDistance.current.offset(offset),
        content = content,
    )
}