package org.hnau.commons.app.projector.fractal.utils.color.tone

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import org.hnau.commons.app.projector.fractal.utils.Distance
import org.hnau.commons.app.model.theme.PaletteType
import org.hnau.commons.app.model.theme.Tone
import org.hnau.commons.app.projector.fractal.utils.color.contrast.Contrast
import org.hnau.commons.app.projector.fractal.utils.color.provider.FractalColorsProvider
import org.hnau.commons.app.projector.fractal.utils.color.provider.local
import org.hnau.commons.app.projector.fractal.utils.local

val LocalBackgroundTone: ProvidableCompositionLocal<Tone> =
    compositionLocalOf { error("Background tone isn't provided") }

val Tone.Companion.localBackground: Tone
    @Composable
    get() = LocalBackgroundTone.current

@Composable
fun SwitchBackgroundTone(
    newBackgroundTone: Tone,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        value = LocalBackgroundTone provides newBackgroundTone,
        content = content,
    )
}

@Composable
fun SwitchBackgroundTone(
    content: @Composable () -> Unit,
) {
    SwitchBackgroundTone(
        newBackgroundTone = FractalColorsProvider.local.getBackgroundTone(
            distance = Distance.local,
        ),
        content = content,
    )
}

@Composable
fun SwitchBackgroundToneToContainer(
    content: @Composable () -> Unit,
) {
    SwitchBackgroundTone(
        newBackgroundTone = FractalColorsProvider.local.getForegroundTone(
            backgroundTone = Tone.localBackground,
            distance = Distance.local,
            contrast = Contrast.container,
            palette = PaletteType.local,
        ),
        content = content,
    )
}