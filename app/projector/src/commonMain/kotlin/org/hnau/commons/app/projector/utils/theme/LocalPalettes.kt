package org.hnau.commons.app.projector.utils.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import org.hnau.commons.app.model.theme.palette.Palettes

val LocalPalettes: ProvidableCompositionLocal<Palettes> =
    compositionLocalOf { error("Palettes isn't presented") }

val Palettes.Companion.local: Palettes
    @Composable
    get() = LocalPalettes.current