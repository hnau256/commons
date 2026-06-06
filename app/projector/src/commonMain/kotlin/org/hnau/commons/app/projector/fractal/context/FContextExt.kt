package org.hnau.commons.app.projector.fractal.context

import androidx.compose.ui.graphics.Color
import org.hnau.commons.app.model.color.dynamic.hct.Hct
import org.hnau.commons.app.model.color.dynamic.hct.HctSolver
import org.hnau.commons.app.model.color.dynamic.palettes.TonalPalette
import org.hnau.commons.app.model.theme.color.Contrast
import org.hnau.commons.app.model.theme.color.Tone
import org.hnau.commons.app.model.theme.fold
import org.hnau.commons.app.model.theme.palette.PaletteType
import org.hnau.commons.app.projector.fractal.utils.BaseWithDecay
import org.hnau.commons.app.projector.fractal.utils.resolve
import org.hnau.commons.kotlin.foldBoolean
import kotlin.math.absoluteValue
import org.hnau.commons.app.model.color.dynamic.contrast.Contrast as ContrastUtils

val FContext.color: Color
    get() = getColor(this@color.tone)

fun FContext.overlay(
    contrast: BaseWithDecay<Contrast>,
): FContext = copy(
    tone = calcTone(
        contrast = contrast,
    ),
)

private val FContext.palette: PaletteType
    get() = PaletteType.resolve(
        mood = mood,
        saturation = saturation,
    )

private fun FContext.calcTone(
    contrast: BaseWithDecay<Contrast>,
): Tone = findContrastedTone(
    contrast = contrast[distance],
)

private fun FContext.getColor(
    tone: Tone,
): Color = palettes.palettes[palette]
    .getHct(tone.raw)
    .toInt()
    .let(::Color)




private fun FContext.findContrastedTone(
    contrast: Contrast,
): Tone {

    val palette = palettes.palettes[palette]

    val baseResult = this@findContrastedTone.tone.lighterOrDarkerWithError(
        lighter = palettes.brightness.fold(
            ifLight = { false },
            ifDark = { true },
        ),
        palette = palette,
        ratio = contrast,
    )

    if (baseResult != null && baseResult.error < 1.0) {
        return baseResult.tone
    }

    val oppositeResult = this@findContrastedTone.tone.lighterOrDarkerWithError(
        lighter = palettes.brightness.fold(
            ifLight = { true },
            ifDark = { false },
        ),
        palette = palette,
        ratio = contrast,
    )

    return when {
        baseResult != null && oppositeResult != null -> {
            if (baseResult.error <= oppositeResult.error) baseResult.tone
            else oppositeResult.tone
        }

        baseResult != null -> baseResult.tone

        oppositeResult != null -> oppositeResult.tone

        else -> (this@findContrastedTone.tone > averageTone).foldBoolean(
            ifTrue = { Tone.min },
            ifFalse = { Tone.max },
        )
    }
}

private val averageTone: Tone = (Tone.min + Tone.max) / 2

private fun Tone.lighterOrDarkerWithError(
    lighter: Boolean,
    palette: TonalPalette,
    ratio: Contrast,
): ToneWithError? = lighter
    .foldBoolean(
        ifTrue = { ContrastUtils.lighter(raw, ratio.contrast) },
        ifFalse = { ContrastUtils.darker(raw, ratio.contrast) }
    )
    ?.let(Tone.Companion::create)
    ?.let { tone ->
        val rawTone = tone.raw
        ToneWithError(
            tone = tone,
            error = run {
                val argb = HctSolver.solveToInt(
                    hueDegrees = palette.hue,
                    chroma = palette.chroma,
                    lstar = rawTone,
                )
                val actualHct = Hct.fromInt(argb)
                val toneError = (rawTone - actualHct.tone).absoluteValue
                val chromaError = (palette.chroma - actualHct.chroma).absoluteValue
                toneError * 10.0 + chromaError
            }
        )
    }

private data class ToneWithError(
    val tone: Tone,
    val error: Double,
)