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
import org.hnau.commons.app.projector.fractal.utils.color.ColorType
import org.hnau.commons.app.projector.fractal.utils.color.contrast.content
import org.hnau.commons.app.projector.fractal.utils.color.contrast.findContrasted
import org.hnau.commons.app.projector.fractal.utils.color.toColor
import org.hnau.commons.app.projector.fractal.utils.color.tone.getHct
import org.hnau.commons.app.projector.fractal.utils.color.tone.localBackground
import org.hnau.commons.app.projector.fractal.utils.local
import org.hnau.commons.kotlin.foldNullable

fun Palettes.getBackgroundTone(
    distance: Distance,
): Tone {

    val (start, step) = brightness.fold(
        ifLight = { 98 to -12 },
        ifDark = { 6 to 8 },
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

fun Palettes.getColor(
    tone: Tone,
    palette: PaletteType,
): Color = palettes[palette]
    .getHct(
        tone = tone,
    )
    .toColor()

fun Palettes.getColor(
    distance: Distance,
    backgroundTone: Tone,
    palette: PaletteType,
    type: ColorType,
): Color {

    val foregroundContrast = when (type) {
        ColorType.Background -> null
        ColorType.Content -> Contrast.content
    }

    val tone = foregroundContrast.foldNullable(
        ifNull = { backgroundTone },
        ifNotNull = { contrast ->
            getForegroundTone(
                backgroundTone = backgroundTone,
                distance = distance,
                contrast = contrast,
                palette = palette,
            )
        }
    )

    return getColor(
        tone = tone,
        palette = palette,
    )
}

@Composable
fun getLocalColor(
    type: ColorType,
): Color = Palettes
    .local
    .getColor(
        distance = Distance.local,
        backgroundTone = Tone.localBackground,
        palette = PaletteType.local,
        type = type,
    )