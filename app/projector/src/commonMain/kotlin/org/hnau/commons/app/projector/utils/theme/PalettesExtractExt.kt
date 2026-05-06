package org.hnau.commons.app.projector.utils.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import org.hnau.commons.app.model.theme.color.Contrast
import org.hnau.commons.app.model.theme.color.Tone
import org.hnau.commons.app.model.theme.fold
import org.hnau.commons.app.model.theme.palette.PaletteType
import org.hnau.commons.app.model.theme.palette.Palettes
import org.hnau.commons.app.projector.fractal.utils.BaseWithDecay
import org.hnau.commons.app.projector.fractal.utils.Distance
import org.hnau.commons.app.projector.fractal.utils.LocalDistance
import org.hnau.commons.app.projector.fractal.utils.LocalPalette
import org.hnau.commons.app.projector.fractal.utils.color.ColorType
import org.hnau.commons.app.projector.fractal.utils.color.contrast.content
import org.hnau.commons.app.projector.fractal.utils.color.contrast.findContrasted
import org.hnau.commons.app.projector.fractal.utils.color.tone.LocalBackgroundTone
import org.hnau.commons.app.projector.fractal.utils.color.tone.getHct

fun Palettes.getBackgroundTone(
    distance: Distance,
): Tone {

    val (start, step) = brightness.fold(
        ifLight = { 98 to -12 },
        ifDark = { 4 to 6 },
    )

    return (start + (step * distance.distance)).let(Tone::create)
}

fun Palettes.getForegroundTone(
    backgroundTone: Tone,
    distance: Distance,
    contrast: BaseWithDecay<Contrast>,
    palette: PaletteType,
): Tone = backgroundTone.findContrasted(
    palette = palettes[palette],
    themeBrightness = brightness,
    ratio = contrast[distance],
)

private fun Palettes.getColor(
    distance: Distance,
    backgroundTone: Tone,
    palette: PaletteType,
    type: ColorType,
): Color = palettes[palette]
    .getHct(
        tone = when (type) {
            ColorType.Background -> backgroundTone
            ColorType.Content -> {
                getForegroundTone(
                    backgroundTone = backgroundTone,
                    distance = distance,
                    contrast = Contrast.content,
                    palette = palette,
                )
            }
        }
    )
    .toInt()
    .let(::Color)

@Composable
fun getLocalColor(
    type: ColorType,
): Color = LocalPalettes
    .current
    .getColor(
        distance = LocalDistance.current,
        backgroundTone = LocalBackgroundTone.current,
        palette = LocalPalette.current,
        type = type,
    )