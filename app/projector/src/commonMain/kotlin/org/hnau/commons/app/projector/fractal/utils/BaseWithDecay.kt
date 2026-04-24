package org.hnau.commons.app.projector.fractal.utils

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.pow

fun interface BaseWithDecay<out T> {

    operator fun get(
        distance: Distance,
    ): T

    companion object
}

private data class BaseWithDecayImpl<out T>(
    val initial: T,
    val decay: Double,
    private val baseline: T,
    private val plus: (T, T) -> T,
    private val minus: (T, T) -> T,
    private val times: (T, Double) -> T,
) : BaseWithDecay<T> {

    override operator fun get(
        distance: Distance,
    ): T = plus(
        baseline,
        times(
            minus(initial, baseline),
            decay.pow(distance.distance),
        ),
    )

    companion object
}

fun <T> BaseWithDecay.Companion.create(
    initial: T,
    decay: Double,
    baseline: T,
    plus: (T, T) -> T,
    minus: (T, T) -> T,
    times: (T, Double) -> T,
): BaseWithDecay<T> = BaseWithDecayImpl(
    initial = initial,
    decay = decay,
    baseline = baseline,
    plus = plus,
    minus = minus,
    times = times,
)

fun BaseWithDecay.Companion.double(
    initial: Double,
    decay: Double,
    baseline: Double = 0.0,
): BaseWithDecay<Double> = BaseWithDecay.create(
    initial = initial,
    decay = decay,
    baseline = baseline,
    plus = Double::plus,
    minus = Double::minus,
    times = Double::times,
)

fun BaseWithDecay.Companion.int(
    initial: Int,
    decay: Double,
    baseline: Int = 0,
): BaseWithDecay<Int> = BaseWithDecay.create(
    initial = initial,
    decay = decay,
    baseline = baseline,
    plus = Int::plus,
    minus = Int::minus,
    times = { value, factor -> (value * factor).toInt() },
)

fun BaseWithDecay.Companion.float(
    initial: Float,
    decay: Double,
    baseline: Float = 0.0f,
): BaseWithDecay<Float> = BaseWithDecay.create(
    initial = initial,
    decay = decay,
    baseline = baseline,
    plus = Float::plus,
    minus = Float::minus,
    times = { value, factor -> (value * factor).toFloat() },
)

fun BaseWithDecay.Companion.dp(
    initial: Dp,
    decay: Double,
    baseline: Dp = 0.dp,
): BaseWithDecay<Dp> = float(
    initial = initial.value,
    decay = decay,
    baseline = baseline.value,
).map(Float::dp)

fun BaseWithDecay.Companion.textUnit(
    initial: TextUnit,
    decay: Double,
    baseline: TextUnit = 0.sp,
): BaseWithDecay<TextUnit> = float(
    initial = initial.value,
    decay = decay,
    baseline = baseline.value,
).map(Float::sp)

inline fun <I, O> BaseWithDecay<I>.map(
    crossinline transform: (I) -> O,
): BaseWithDecay<O> = BaseWithDecay { distance ->
    get(distance).let(transform)
}