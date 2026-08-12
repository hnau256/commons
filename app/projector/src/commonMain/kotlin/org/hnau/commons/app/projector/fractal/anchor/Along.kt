package org.hnau.commons.app.projector.fractal.anchor

import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.TwoWayConverter
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp

@JvmInline
value class Along(
    val along: Float,
) : Comparable<Along> {

    override fun compareTo(
        other: Along,
    ): Int = along.compareTo(
        other = other.along,
    )

    inline fun combine(
        other: Along,
        block: (Float, Float) -> Float,
    ): Along = block(
        along,
        other.along,
    ).let(::Along)

    operator fun plus(
        other: Along,
    ): Along = combine(
        other = other,
        block = Float::plus,
    )

    operator fun minus(
        other: Along,
    ): Along = combine(
        other = other,
        block = Float::minus,
    )

    companion object {

        val VisibilityThreshold: Along
            @Composable
            get() = with(LocalDensity.current) {
                Dp.VisibilityThreshold.toPx().let(::Along)
            }

        val twoWayConverter: TwoWayConverter<Along, AnimationVector1D> = TwoWayConverter(
            convertToVector = { AnimationVector1D(it.along) },
            convertFromVector = { vector -> Along(vector.value) }
        )
    }
}