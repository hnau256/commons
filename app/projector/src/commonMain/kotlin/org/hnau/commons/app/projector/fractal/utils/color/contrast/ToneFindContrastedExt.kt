package org.hnau.commons.app.projector.fractal.utils.color.contrast

import org.hnau.commons.app.model.theme.ThemeBrightness
import org.hnau.commons.app.model.theme.fold
import org.hnau.commons.app.model.color.dynamic.hct.Hct
import org.hnau.commons.app.model.color.dynamic.hct.HctSolver
import org.hnau.commons.app.model.color.dynamic.palettes.TonalPalette
import org.hnau.commons.app.model.theme.Tone
import org.hnau.commons.kotlin.foldBoolean
import kotlin.math.absoluteValue
import org.hnau.commons.app.model.color.dynamic.contrast.Contrast as ContrastUtils

fun Tone.findContrasted(
    palette: TonalPalette,
    themeBrightness: ThemeBrightness,
    ratio: Contrast,
): Tone {

    val baseResult = lighterOrDarkerWithError(
        lighter = themeBrightness.fold(
            ifLight = { false },
            ifDark = { true },
        ),
        palette = palette,
        ratio = ratio,
    )

    if (baseResult != null && baseResult.error < 0.1) {
        return baseResult.tone
    }

    val oppositeResult = lighterOrDarkerWithError(
        lighter = themeBrightness.fold(
            ifLight = { true },
            ifDark = { false },
        ),
        palette = palette,
        ratio = ratio,
    )

    return when {
        baseResult != null && oppositeResult != null -> {
            if (baseResult.error <= oppositeResult.error) baseResult.tone
            else oppositeResult.tone
        }

        baseResult != null -> baseResult.tone

        oppositeResult != null -> oppositeResult.tone

        else -> (this > averageTone).foldBoolean(
            ifTrue = { Tone.Companion.min },
            ifFalse = { Tone.Companion.max },
        )
    }
}

private val averageTone: Tone = (Tone.Companion.min + Tone.Companion.max) / 2

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
                val argb = HctSolver.solveToInt(palette.hue, palette.chroma, rawTone)
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