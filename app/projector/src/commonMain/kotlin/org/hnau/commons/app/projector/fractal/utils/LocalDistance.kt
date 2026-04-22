package org.hnau.commons.app.projector.fractal.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf

val LocalDistance: ProvidableCompositionLocal<Distance> = compositionLocalOf { Distance.zero }

val Distance.Companion.local: Distance
    @Composable
    get() = LocalDistance.current

@Composable
fun OffsetDistance(
    offset: Int,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        value = LocalDistance provides Distance.local.offset(offset),
        content = content,
    )
}