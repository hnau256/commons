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
            baseline: Double = 0.0,
        ): BaseWithDecay<Double> = BaseWithDecay(
            base = base,
            decay = decay,
            times = { value, factor ->
                baseline + (value - baseline) * factor
            },
        )

        fun float(
            base: Float,
            decay: Double,
            baseline: Float = 0.0f,
        ): BaseWithDecay<Float> = BaseWithDecay(
            base = base,
            decay = decay,
            times = { value, factor ->
                (baseline + (value - baseline) * factor).toFloat()
            },
        )

        fun dp(
            base: Dp,
            decay: Double,
            baseline: Dp = 0.dp,
        ): BaseWithDecay<Dp> = BaseWithDecay(
            base = base,
            decay = decay,
            times = { value, factor ->
                (baseline.value + (value.value - baseline.value) * factor).dp
            },
        )

        fun textUnit(
            base: TextUnit,
            decay: Double,
            baseline: TextUnit = 0.sp,
        ): BaseWithDecay<TextUnit> = BaseWithDecay(
            base = base,
            decay = decay,
            times = { value, factor ->
                (baseline.value + (value.value - baseline.value) * factor).sp
            },
        )
    }
}