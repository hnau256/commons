package org.hnau.commons.app.model.theme.color

import org.hnau.commons.kotlin.lerp
import org.hnau.commons.kotlin.requireInRange
import kotlin.jvm.JvmInline

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

        fun create(
            raw: Double
        ): Tone {
            requireInRange(
                valueLabel = "Tone",
                value = raw,
                range = minRaw..maxRaw,
            )
            return Tone(raw)
        }

        val min: Tone
            get() = Tone(minRaw)

        val avg: Tone = min.combineWith(
            other = max,
        ) { min, max ->
            (min + max) / 2
        }

        val max: Tone
            get() = Tone(maxRaw)

        private val minRaw: Double
            get() = 0.0

        private val maxRaw: Double
            get() = 100.0
    }
}

fun Tone.Companion.lerp(
    start: Tone,
    stop: Tone,
    fraction: Double,
): Tone = lerp(
    start = start.raw,
    stop = stop.raw,
    fraction = fraction,
).let(Tone::create)