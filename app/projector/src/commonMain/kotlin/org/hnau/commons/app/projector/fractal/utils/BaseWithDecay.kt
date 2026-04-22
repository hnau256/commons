package org.hnau.commons.app.projector.fractal.utils

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.pow

data class BaseWithDecay<out T>(
    val base: T,
    val decay: Double,
    private val times: (T, Double) -> T,
) {

    operator fun get(
        distance: Distance,
    ): T = times(
        base,
        decay.pow(distance.distance),
    )

    companion object {

        fun double(
            base: Double,
            decay: Double,
        ): BaseWithDecay<Double> = BaseWithDecay(
            base = base,
            decay = decay,
            times = Double::times,
        )

        fun float(
            base: Float,
            decay: Double,
        ): BaseWithDecay<Float> = BaseWithDecay(
            base = base,
            decay = decay,
            times = { value, factor ->
                value
                    .times(factor)
                    .toFloat()
            },
        )

        fun dp(
            base: Dp,
            decay: Double,
        ): BaseWithDecay<Dp> = BaseWithDecay(
            base = base,
            decay = decay,
            times = { value, factor ->
                value
                    .value
                    .times(factor)
                    .dp
            },
        )

        fun textUnit(
            base: TextUnit,
            decay: Double,
        ): BaseWithDecay<TextUnit> = BaseWithDecay(
            base = base,
            decay = decay,
            times = { value, factor ->
                value
                    .value
                    .times(factor)
                    .sp
            },
        )
    }
}