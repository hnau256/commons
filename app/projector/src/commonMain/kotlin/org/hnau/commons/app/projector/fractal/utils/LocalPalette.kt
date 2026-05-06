package org.hnau.commons.app.projector.fractal.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import org.hnau.commons.app.model.theme.palette.PaletteType

val LocalPalette: ProvidableCompositionLocal<PaletteType> =
    compositionLocalOf { error("Local palette isn't provided") }

@Composable
fun SwitchPalette(
    newPalette: PaletteType,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        value = LocalPalette provides newPalette,
        content = content,
    )
}