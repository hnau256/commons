package org.hnau.commons.app.projector.fractal.anchor

import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.TwoWayConverter

@JvmInline
value class AlongPx(
    val along: Float,
) : Comparable<AlongPx> {

    override fun compareTo(
        other: AlongPx,
    ): Int = along.compareTo(
        other = other.along,
    )

    inline fun combine(
        other: AlongPx,
        block: (Float, Float) -> Float,
    ): AlongPx = block(
        along,
        other.along,
    ).let(::AlongPx)

    operator fun plus(
        other: AlongPx,
    ): AlongPx = combine(
        other = other,
        block = Float::plus,
    )

    operator fun minus(
        other: AlongPx,
    ): AlongPx = combine(
        other = other,
        block = Float::minus,
    )

    companion object {

        val twoWayConverter: TwoWayConverter<AlongPx, AnimationVector1D> = TwoWayConverter(
            convertToVector = { AnimationVector1D(it.along) },
            convertFromVector = { vector -> AlongPx(vector.value) }
        )
    }
}