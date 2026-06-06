package org.hnau.commons.app.projector.fractal.context

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import org.hnau.commons.app.projector.fractal.utils.Mood
import org.hnau.commons.app.projector.fractal.utils.Saturation

val LocalFContext: ProvidableCompositionLocal<FContext> =
    compositionLocalOf { error("LocalFContext is not provided") }

@Composable
fun UpdateFContext(
    update: FContext.() -> FContext,
    content: @Composable FContext.() -> Unit,
) {
    val current = LocalFContext.current
    val updated = update(current)
    CompositionLocalProvider(
        value = LocalFContext provides updated,
        content = { updated.content() },
    )
}

@Composable
fun UpdateFContext(
    mood: Mood = LocalFContext.current.mood,
    saturation: Saturation = LocalFContext.current.saturation,
    content: @Composable FContext.() -> Unit,
) {
    UpdateFContext(
        update = {
            copy(
                saturation = saturation,
                mood = mood,
            )
        },
        content = content,
    )
}