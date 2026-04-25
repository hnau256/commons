package org.hnau.commons.app.projector.fractal.utils.size

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.hnau.commons.app.projector.fractal.utils.BaseWithDecay
import org.hnau.commons.app.projector.fractal.utils.Distance
import org.hnau.commons.app.projector.fractal.utils.float
import org.hnau.commons.app.projector.fractal.utils.map
import org.hnau.commons.gen.enumvalues.annotations.EnumValues
import kotlin.math.roundToInt

@JvmInline
value class Scale(
    val scale: Float,
) {

    @EnumValues
    enum class Type { Content, Space }
}

private val scaleBaseWithDecays: ScaleTypeValues<BaseWithDecay<Scale>> = ScaleTypeValues(
    content = BaseWithDecay.float(
        initial = 1f,
        decay = 0.75,
    ),
    space = BaseWithDecay.float(
        initial = 1f,
        decay = 0.5,
    )
).map { floatBaseWithDecay ->
    floatBaseWithDecay.map(::Scale)
}

private val scaleCache = HashMap<Distance, ScaleTypeValues<Scale>>()

val Distance.scale: ScaleTypeValues<Scale>
    get() = scaleCache.getOrPut(this) {
        scaleBaseWithDecays.map { scaleBaseWithDecay ->
            scaleBaseWithDecay[this]
        }
    }

fun Dp.scale(
    scale: Scale,
    step: Dp = 4.dp,
): Dp = value
    .times(scale.scale)
    .div(step.value)
    .roundToInt()
    .times(step.value)
    .dp
    .coerceAtLeast(step)

fun TextUnit.scale(
    scale: Scale,
    step: TextUnit = 4.sp,
): TextUnit = value
    .times(scale.scale)
    .div(step.value)
    .roundToInt()
    .times(step.value)
    .coerceAtLeast(step.value)
    .sp

