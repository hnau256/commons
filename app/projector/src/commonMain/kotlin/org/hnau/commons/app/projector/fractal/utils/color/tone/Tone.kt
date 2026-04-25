package org.hnau.commons.app.projector.fractal.utils.color.tone

import org.hnau.commons.app.projector.dynamiccolor.hct.Hct
import org.hnau.commons.app.projector.dynamiccolor.palettes.TonalPalette

@JvmInline
value class Tone private constructor(
    val raw: Double,
) : Comparable<Tone> {

    override fun compareTo(
        other: Tone
    ): Int = raw.compareTo(
        other = other.raw,
    )

    inline fun map(
        transform: (Double) -> Double,
    ): Tone = create(
        raw = transform(raw),
    )

    inline fun combineWith(
        other: Tone,
        combine: (Double, Double) -> Double,
    ): Tone = map { tone ->
        combine(tone, other.raw)
    }

    operator fun plus(
        other: Tone,
    ): Tone = combineWith(
        other = other,
        combine = Double::plus,
    )

    operator fun minus(
        other: Tone,
    ): Tone = combineWith(
        other = other,
        combine = Double::minus,
    )

    operator fun times(
        factor: Number,
    ): Tone = map { tone ->
        tone * factor.toDouble()
    }

    operator fun div(
        factor: Number,
    ): Tone = map { tone ->
        tone / factor.toDouble()
    }

    companion object {

        private const val minRaw = 0.0
        private const val maxRaw = 100.0

        fun create(
            raw: Double
        ): Tone = Tone(
            raw = raw.coerceIn(minRaw, maxRaw)
        )

        val min: Tone
            get() = Tone(minRaw)

        val max: Tone
            get() = Tone(maxRaw)
    }
}

fun TonalPalette.getHct(
    tone: Tone,
): Hct = getHct(
    tone = tone.raw,
)