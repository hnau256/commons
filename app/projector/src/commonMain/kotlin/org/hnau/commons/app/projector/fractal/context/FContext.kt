package org.hnau.commons.app.projector.fractal.context

import androidx.compose.ui.graphics.Color
import org.hnau.commons.app.model.theme.ThemeBrightnessValues
import org.hnau.commons.app.model.theme.color.Contrast
import org.hnau.commons.app.model.theme.color.Tone
import org.hnau.commons.app.model.theme.palette.PaletteType
import org.hnau.commons.app.model.theme.palette.Palettes
import org.hnau.commons.app.projector.fractal.utils.BaseWithDecay
import org.hnau.commons.app.projector.fractal.utils.Distance
import org.hnau.commons.app.projector.fractal.utils.Mood
import org.hnau.commons.app.projector.fractal.utils.Saturation
import org.hnau.commons.app.projector.fractal.utils.container
import org.hnau.commons.app.projector.fractal.utils.content
import org.hnau.commons.app.projector.fractal.utils.offset
import org.hnau.commons.app.projector.fractal.utils.resolve
import org.hnau.commons.kotlin.ifFalse
import org.hnau.commons.kotlin.ifNull

class FContext private constructor(
    val distance: Distance,
    val palettes: Palettes,
    val mood: Mood,
    val customContainer: Container?,
) {

    data class Container(
        val saturation: Saturation,
        val tone: Tone,
    )

    fun changePalettes(
        palettes: Palettes,
    ): FContext = FContext(
        distance = distance,
        palettes = palettes,
        mood = mood,
        customContainer = customContainer,
    )

    fun makeDeeper(
        offset: Int,
        resetOverlay: Boolean,
    ): FContext = FContext(
        distance = distance.offset(offset),
        palettes = palettes,
        mood = mood,
        customContainer = resetOverlay.ifFalse { customContainer },
    )

    fun overlay(
        saturation: Saturation,
        contrast: BaseWithDecay<Contrast> = Contrast.container,
    ): FContext = FContext(
        distance = distance,
        palettes = palettes,
        mood = mood,
        customContainer = Container(
            saturation = saturation,
            tone = calcTone(
                saturation = saturation,
                contrast = contrast,
            )
        ),
    )

    val containerColor: Color
        get() = container.run {
            getColor(
                saturation = saturation,
                tone = tone,
            )
        }

    fun getContentColor(
        saturation: Saturation,
        contrast: BaseWithDecay<Contrast> = Contrast.content,
    ): Color = getColor(
        saturation = saturation,
        tone = calcTone(
            saturation = saturation,
            contrast = contrast,
        )
    )

    fun changeMood(
        mood: Mood,
    ): FContext = FContext(
        distance = distance,
        palettes = palettes,
        mood = mood,
        customContainer = customContainer,
    )

    private val container: Container
        get() = customContainer.ifNull {
            val (start, step) = distanceBackgroundToneStartsAndSteps[palettes.brightness]
            Container(
                saturation = Saturation.default,
                tone = (start + (step * distance.distance)).let(Tone::create)
            )
        }

    private fun getPalette(
        saturation: Saturation,
    ): PaletteType = PaletteType.resolve(
        mood = mood,
        saturation = saturation,
    )

    private fun calcTone(
        saturation: Saturation,
        contrast: BaseWithDecay<Contrast>,
    ): Tone = container.tone.let { container ->
        FContextUtils.findContrastedTone(
            tone = container,
            contrast = contrast[distance],
            palette = palettes.palettes[getPalette(saturation)],
            brightness = palettes.brightness,
        )
    }

    private fun getColor(
        saturation: Saturation,
        tone: Tone,
    ): Color = palettes.palettes[getPalette(saturation)]
        .getHct(tone.raw.toDouble())
        .toInt()
        .let(::Color)

    companion object {

        fun create(
            palettes: Palettes,
        ): FContext = FContext(
            distance = Distance.zero,
            palettes = palettes,
            mood = Mood.default,
            customContainer = null,
        )

        private val distanceBackgroundToneStartsAndSteps: ThemeBrightnessValues<Pair<Int, Int>> =
            ThemeBrightnessValues(
                dark = 4 to 6,
                light = 98 to -12,
            )
    }
}