package org.hnau.commons.app.projector.fractal.utils.color.contrast

import org.hnau.commons.app.projector.fractal.utils.BaseWithDecay
import org.hnau.commons.app.projector.fractal.utils.double
import org.hnau.commons.app.projector.fractal.utils.map
import org.hnau.commons.app.projector.dynamiccolor.contrast.Contrast as ContrastUtils

@JvmInline
value class Contrast(
    val contrast: Double,
) : Comparable<Contrast> {

    override fun compareTo(
        other: Contrast
    ): Int = contrast.compareTo(
        other = other.contrast,
    )

    inline fun map(
        transform: (Double) -> Double,
    ): Contrast = Contrast(
        contrast = transform(contrast),
    )

    inline fun combineWith(
        other: Contrast,
        combine: (Double, Double) -> Double,
    ): Contrast = map { tone ->
        combine(tone, other.contrast)
    }

    operator fun plus(
        other: Contrast,
    ): Contrast = combineWith(
        other = other,
        combine = Double::plus,
    )

    operator fun minus(
        other: Contrast,
    ): Contrast = combineWith(
        other = other,
        combine = Double::minus,
    )

    operator fun times(
        factor: Number,
    ): Contrast = map { tone ->
        tone * factor.toDouble()
    }

    operator fun div(
        factor: Number,
    ): Contrast = map { tone ->
        tone / factor.toDouble()
    }

    companion object {

        val min: Contrast
            get() = Contrast(ContrastUtils.RATIO_MIN)

        val max: Contrast
            get() = Contrast(ContrastUtils.RATIO_MAX)

        val c30: Contrast
            get() = Contrast(ContrastUtils.RATIO_30)

        val c45: Contrast
            get() = Contrast(ContrastUtils.RATIO_45)

        val c70: Contrast
            get() = Contrast(ContrastUtils.RATIO_70)

        val container: BaseWithDecay<Contrast> = BaseWithDecay.contrast(
            initial = Contrast(2.0),
            decay = 0.5,
        )

        val content: BaseWithDecay<Contrast> = BaseWithDecay.contrast(
            initial = Contrast(7.0),
            decay = 0.75,
        )
    }
}

fun BaseWithDecay.Companion.contrast(
    initial: Contrast,
    decay: Double,
): BaseWithDecay<Contrast> = BaseWithDecay.double(
    initial = initial.contrast,
    decay = decay,
    baseline = Contrast.min.contrast
).map(::Contrast)