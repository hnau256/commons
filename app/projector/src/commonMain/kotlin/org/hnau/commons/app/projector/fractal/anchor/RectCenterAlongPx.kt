package org.hnau.commons.app.projector.fractal.anchor

import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.TwoWayConverter

@JvmInline
value class RectCenterAlongPx(
    val along: Float,
) : Comparable<RectCenterAlongPx> {

    override fun compareTo(
        other: RectCenterAlongPx,
    ): Int = along.compareTo(
        other = other.along,
    )

    inline fun combine(
        other: RectCenterAlongPx,
        block: (Float, Float) -> Float,
    ): RectCenterAlongPx = block(
        along,
        other.along,
    ).let(::RectCenterAlongPx)

    operator fun plus(
        other: RectCenterAlongPx,
    ): RectCenterAlongPx = combine(
        other = other,
        block = Float::plus,
    )

    operator fun minus(
        other: RectCenterAlongPx,
    ): RectCenterAlongPx = combine(
        other = other,
        block = Float::minus,
    )

    companion object {

        val twoWayConverter: TwoWayConverter<RectCenterAlongPx, AnimationVector1D> = TwoWayConverter(
            convertToVector = { AnimationVector1D(it.along) },
            convertFromVector = { vector -> RectCenterAlongPx(vector.value) }
        )
    }
}