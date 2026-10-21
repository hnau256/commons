package org.hnau.commons.app.model.theme.color

import org.hnau.commons.kotlin.requireInRange
import kotlin.jvm.JvmInline

@JvmInline
value class Chroma private constructor(
    val raw: Int,
) : Comparable<Chroma> {

    override fun compareTo(
        other: Chroma
    ): Int = raw.compareTo(
        other = other.raw,
    )

    inline fun map(
        transform: (Int) -> Int,
    ): Chroma = create(
        raw = transform(raw),
    )

    inline fun combineWith(
        other: Chroma,
        combine: (Int, Int) -> Int,
    ): Chroma = map { tone ->
        combine(tone, other.raw)
    }

    operator fun plus(
        other: Chroma,
    ): Chroma = combineWith(
        other = other,
        combine = Int::plus,
    )

    operator fun minus(
        other: Chroma,
    ): Chroma = combineWith(
        other = other,
        combine = Int::minus,
    )

    operator fun times(
        factor: Number,
    ): Chroma = map { tone ->
        tone * factor.toInt()
    }

    operator fun div(
        factor: Number,
    ): Chroma = map { tone ->
        tone / factor.toInt()
    }

    companion object {

        fun create(
            raw: Int
        ): Chroma {
            requireInRange(
                valueLabel = "Chroma",
                value = raw,
                range = minRaw..maxRaw,
            )
            return Chroma(raw)
        }

        val min: Chroma
            get() = Chroma(minRaw)

        val max: Chroma
            get() = Chroma(maxRaw)

        private val minRaw: Int
            get() = 0

        private val maxRaw: Int
            get() = 100
    }
}