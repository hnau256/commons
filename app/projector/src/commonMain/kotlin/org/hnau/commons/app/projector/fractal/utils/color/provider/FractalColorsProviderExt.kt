package org.hnau.commons.app.projector.fractal.utils.color.provider

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import org.hnau.commons.app.projector.fractal.utils.color.contrast.Contrast
import org.hnau.commons.app.projector.fractal.utils.Distance
import org.hnau.commons.app.projector.fractal.utils.color.tone.Tone
import org.hnau.commons.app.projector.fractal.utils.color.ColorType
import org.hnau.commons.app.projector.fractal.utils.color.PaletteType
import org.hnau.commons.app.projector.fractal.utils.local
import org.hnau.commons.app.projector.fractal.utils.color.tone.localBackground
import org.hnau.commons.kotlin.foldNullable

fun FractalColorsProvider.getColor(
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
): Color = FractalColorsProvider
    .local
    .getColor(
        distance = Distance.local,
        backgroundTone = Tone.localBackground,
        palette = PaletteType.local,
        type = type,
    )