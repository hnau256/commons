package org.hnau.commons.app.model.theme.color

import org.hnau.commons.kotlin.requireInRange
import kotlin.jvm.JvmInline

@JvmInline
value class Tone private constructor(
    val raw: Int,
) : Comparable<Tone> {

    override fun compareTo(
        other: Tone
    ): Int = raw.compareTo(
        other = other.raw,
    )

    inline fun map(
        transform: (Int) -> Int,
    ): Tone = create(
        raw = transform(raw),
    )

    inline fun combineWith(
        other: Tone,
        combine: (Int, Int) -> Int,
    ): Tone = map { tone ->
        combine(tone, other.raw)
    }

    operator fun plus(
        other: Tone,
    ): Tone = combineWith(
        other = other,
        combine = Int::plus,
    )

    operator fun minus(
        other: Tone,
    ): Tone = combineWith(
        other = other,
        combine = Int::minus,
    )

    operator fun times(
        factor: Number,
    ): Tone = map { tone ->
        tone * factor.toInt()
    }

    operator fun div(
        factor: Number,
    ): Tone = map { tone ->
        tone / factor.toInt()
    }

    companion object {

        fun create(
            raw: Int
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

        private val minRaw: Int
            get() = 0

        private val maxRaw: Int
            get() = 100
    }
}