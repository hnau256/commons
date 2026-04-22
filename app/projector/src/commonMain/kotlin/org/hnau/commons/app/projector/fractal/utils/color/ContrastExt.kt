package org.hnau.commons.app.projector.fractal.utils.color

import org.hnau.commons.app.model.theme.ThemeBrightness
import org.hnau.commons.app.model.theme.fold
import org.hnau.commons.app.projector.dynamiccolor.contrast.Contrast
import org.hnau.commons.app.projector.dynamiccolor.hct.Hct
import org.hnau.commons.app.projector.dynamiccolor.hct.HctSolver
import org.hnau.commons.app.projector.dynamiccolor.palettes.TonalPalette
import org.hnau.commons.kotlin.foldBoolean
import kotlin.math.absoluteValue

fun Contrast.lighterOrDarker(
    tone: Double,
    palette: TonalPalette,
    themeBrightness: ThemeBrightness,
    ratio: Double,
): Double {

    val baseResult = lighterOrDarkerWithError(
        tone = tone,
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
        tone = tone,
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

        else -> (tone > 50.0).foldBoolean(
            ifTrue = { 0.0 },
            ifFalse = { 100.0 },
        )
    }
}

private fun Contrast.lighterOrDarkerWithError(
    tone: Double,
    lighter: Boolean,
    palette: TonalPalette,
    ratio: Double,
): ToneWithError? = lighter
    .foldBoolean(
        ifTrue = { lighter(tone, ratio) },
        ifFalse = { darker(tone, ratio) }
    )
    ?.let { tone ->
        ToneWithError(
            tone = tone,
            error = run {
                val argb = HctSolver.solveToInt(palette.hue, palette.chroma, tone)
                val actualHct = Hct.fromInt(argb)
                val toneError = (tone - actualHct.tone).absoluteValue
                val chromaError = (palette.chroma - actualHct.chroma).absoluteValue
                toneError * 10.0 + chromaError
            }
        )
    }

private data class ToneWithError(
    val tone: Double,
    val error: Double,
)