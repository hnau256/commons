package org.hnau.commons.app.projector.fractal.context

import androidx.compose.ui.graphics.Color
import org.hnau.commons.app.model.color.dynamic.hct.Hct
import org.hnau.commons.app.model.color.dynamic.hct.HctSolver
import org.hnau.commons.app.model.color.dynamic.palettes.TonalPalette
import org.hnau.commons.app.model.theme.ThemeBrightnessValues
import org.hnau.commons.app.model.theme.color.Contrast
import org.hnau.commons.app.model.theme.color.Tone
import org.hnau.commons.app.model.theme.fold
import org.hnau.commons.app.model.theme.palette.PaletteType
import org.hnau.commons.app.projector.fractal.utils.BaseWithDecay
import org.hnau.commons.app.projector.fractal.utils.Distance
import org.hnau.commons.app.projector.fractal.utils.container
import org.hnau.commons.app.projector.fractal.utils.contentBySaturation
import org.hnau.commons.app.projector.fractal.utils.resolve
import org.hnau.commons.kotlin.foldBoolean
import org.hnau.commons.kotlin.ifNull
import kotlin.math.absoluteValue
import org.hnau.commons.app.model.color.dynamic.contrast.Contrast as ContrastUtils

val FContext.containerColor: Color
    get() = getColor(containerTone)

val FContext.contentColor: Color
    get() = getColor(
        tone = calcTone(
            contrast = Contrast.contentBySaturation[saturation],
        )
    )

fun FContext.overlay(
    contrast: BaseWithDecay<Contrast> = Contrast.container,
): FContext = copy(
    customContainerTone = calcTone(
        contrast = contrast,
    ),
)

private val FContext.containerTone: Tone
    get() = customContainerTone
        .ifNull { backgroundToneCalculators[palettes.brightness](distance) }

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
    .getHct(tone.raw.toDouble())
    .toInt()
    .let(::Color)


private val backgroundToneCalculators: ThemeBrightnessValues<(Distance) -> Tone> =
    ThemeBrightnessValues(
        dark = 6 to 4,
        light = 98 to -12,
    ).map { (start, step) ->
        { distance: Distance -> (start + (step * distance.distance)).let(Tone::create) }
    }

private fun FContext.findContrastedTone(
    contrast: Contrast,
): Tone {

    val palette = palettes.palettes[palette]

    val baseResult = containerTone.lighterOrDarkerWithError(
        lighter = palettes.brightness.fold(
            ifLight = { false },
            ifDark = { true },
        ),
        palette = palette,
        ratio = contrast,
    )

    if (baseResult != null && baseResult.error < 0.1) {
        return baseResult.tone
    }

    val oppositeResult = containerTone.lighterOrDarkerWithError(
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

        else -> (containerTone > averageTone).foldBoolean(
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
        ifTrue = { ContrastUtils.lighter(raw.toDouble(), ratio.contrast.toDouble()) },
        ifFalse = { ContrastUtils.darker(raw.toDouble(), ratio.contrast.toDouble()) }
    )
    ?.toInt()
    ?.let(Tone.Companion::create)
    ?.let { tone ->
        val rawTone = tone.raw
        ToneWithError(
            tone = tone,
            error = run {
                val argb = HctSolver.solveToInt(
                    hueDegrees = palette.hue,
                    chroma = palette.chroma,
                    lstar = rawTone.toDouble(),
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