package org.hnau.commons.app.projector.fractal.utils.color.tone

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import org.hnau.commons.app.model.theme.color.Contrast
import org.hnau.commons.app.model.theme.color.Tone
import org.hnau.commons.app.projector.fractal.utils.Distance
import org.hnau.commons.app.projector.fractal.utils.LocalDistance
import org.hnau.commons.app.projector.fractal.utils.LocalPalette
import org.hnau.commons.app.projector.fractal.utils.color.contrast.container
import org.hnau.commons.app.projector.utils.theme.getBackgroundTone
import org.hnau.commons.app.projector.utils.theme.getForegroundTone
import org.hnau.commons.app.projector.utils.theme.LocalPalettes

val LocalBackgroundTone: ProvidableCompositionLocal<Tone> =
    compositionLocalOf { error("Background tone isn't provided") }

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
        newBackgroundTone = LocalPalettes.current.getBackgroundTone(
            distance = LocalDistance.current,
        ),
        content = content,
    )
}

@Composable
fun SwitchBackgroundToneToContainer(
    content: @Composable () -> Unit,
) {
    SwitchBackgroundTone(
        newBackgroundTone = LocalPalettes.current.getForegroundTone(
            backgroundTone = LocalBackgroundTone.current,
            distance = LocalDistance.current,
            contrast = Contrast.container,
            palette = LocalPalette.current,
        ),
        content = content,
    )
}