package org.hnau.commons.app.projector.fractal.context

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import org.hnau.commons.app.model.color.dynamic.hct.Hct
import org.hnau.commons.app.model.color.dynamic.hct.HctSolver
import org.hnau.commons.app.model.color.dynamic.palettes.TonalPalette
import org.hnau.commons.app.model.theme.color.Contrast
import org.hnau.commons.app.model.theme.color.Tone
import org.hnau.commons.app.model.theme.fold
import org.hnau.commons.app.model.theme.palette.PaletteType
import org.hnau.commons.app.projector.fractal.distance.LocalDistance
import org.hnau.commons.app.projector.fractal.utils.BaseWithDecay
import org.hnau.commons.app.projector.fractal.utils.SaturationValues
import org.hnau.commons.app.projector.fractal.utils.container
import org.hnau.commons.app.projector.fractal.utils.content
import org.hnau.commons.app.projector.fractal.utils.fold
import org.hnau.commons.kotlin.foldBoolean
import kotlin.math.absoluteValue
import org.hnau.commons.app.model.color.dynamic.contrast.Contrast as ContrastUtils

val FContext.color: Color
    get() = palettes
        .palettes[palette]
        .getHct(tone.raw)
        .toInt()
        .let(::Color)

@Composable
fun FContext.overlay(
    contrast: SaturationValues<BaseWithDecay<Contrast>>,
): FContext = copy(
    tone = calcTone(
        contrast = contrast[mood.saturation],
    ),
)

@Composable
fun FContext.contentOverlay(): FContext = overlay(
    contrast = Contrast.content,
)

@Composable
fun FContext.containerOverlay(): FContext = overlay(
    contrast = Contrast.container,
)

private val FContext.palette: PaletteType
    get() = mood.fold(
        ifActive = { importance ->
            importance.fold(
                ifPrimary = { PaletteType.Primary },
                ifSecondary = { PaletteType.Secondary },
                ifTertiary = { PaletteType.Tertiary },
            )
        },
        ifNeutral = { PaletteType.Neutral },
        ifError = { PaletteType.Error }
    )

@Composable
private fun FContext.calcTone(
    contrast: BaseWithDecay<Contrast>,
): Tone = findContrastedTone(
    contrast = contrast[LocalDistance.current],
)


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
                toneError * 0.9 + chromaError * 0.1
            }
        )
    }

private data class ToneWithError(
    val tone: Tone,
    val error: Double,
)