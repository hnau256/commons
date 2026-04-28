package org.hnau.commons.app.model.theme.color

import kotlin.jvm.JvmInline
import org.hnau.commons.app.model.color.dynamic.contrast.Contrast as ContrastUtils

@JvmInline
value class Contrast(
    val contrast: Int,
) : Comparable<Contrast> {

    override fun compareTo(
        other: Contrast
    ): Int = contrast.compareTo(
        other = other.contrast,
    )

    inline fun map(
        transform: (Int) -> Int,
    ): Contrast = Contrast(
        contrast = transform(contrast),
    )

    inline fun combineWith(
        other: Contrast,
        combine: (Int, Int) -> Int,
    ): Contrast = map { tone ->
        combine(tone, other.contrast)
    }

    operator fun plus(
        other: Contrast,
    ): Contrast = combineWith(
        other = other,
        combine = Int::plus,
    )

    operator fun minus(
        other: Contrast,
    ): Contrast = combineWith(
        other = other,
        combine = Int::minus,
    )

    operator fun times(
        factor: Number,
    ): Contrast = map { tone ->
        tone * factor.toInt()
    }

    operator fun div(
        factor: Number,
    ): Contrast = map { tone ->
        tone / factor.toInt()
    }

    companion object {

        val zero: Contrast
            get() = Contrast(0)

        val min: Contrast
            get() = Contrast(ContrastUtils.RATIO_MIN.toInt())

        val max: Contrast
            get() = Contrast(ContrastUtils.RATIO_MAX.toInt())

        val c30: Contrast
            get() = Contrast(ContrastUtils.RATIO_30.toInt())

        val c45: Contrast
            get() = Contrast(ContrastUtils.RATIO_45.toInt())

        val c70: Contrast
            get() = Contrast(ContrastUtils.RATIO_70.toInt())
    }
}