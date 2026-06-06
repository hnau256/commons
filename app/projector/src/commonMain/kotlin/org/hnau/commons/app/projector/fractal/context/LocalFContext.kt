package org.hnau.commons.app.projector.fractal.context

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf

val LocalFContext: ProvidableCompositionLocal<FContext> =
    compositionLocalOf { error("LocalFContext is not provided") }

@Composable
fun FContext(
    update: @Composable FContext.() -> FContext,
    content: @Composable () -> Unit,
) {
    val current = LocalFContext.current
    val updated = update(current)
    CompositionLocalProvider(
        value = LocalFContext provides updated,
        content = { content() },
    )
}