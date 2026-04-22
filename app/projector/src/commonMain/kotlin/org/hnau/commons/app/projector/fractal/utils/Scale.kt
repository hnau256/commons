package org.hnau.commons.app.projector.fractal.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.hnau.commons.gen.enumvalues.annotations.EnumValues
import kotlin.math.roundToInt

@JvmInline
value class Scale(
    val scale: Float,
) {

    companion object {

        val single: Scale
            get() = Scale(1f)
    }
}

@EnumValues
enum class ScalableType { Space, Content }

private val scalableBaseWithDecays: ScalableTypeValues<BaseWithDecay<Scale>> = ScalableTypeValues(
    space = 0.5,
    content = 0.7,
).map { decay ->
    BaseWithDecay(
        base = Scale.single,
        decay = decay,
        times = { scale, factor ->
            scale
                .scale
                .times(factor)
                .toFloat()
                .let(::Scale)
        }
    )

}

fun Distance.scale(
    scalableType: ScalableType,
): Scale = scalableBaseWithDecays[scalableType][this]

val Distance.spaceScale: Scale
    get() = scale(scalableType = ScalableType.Space)

val localSpaceScale: Scale
    @Composable
    get() = Distance.local.spaceScale

val Distance.contentScale: Scale
    get() = scale(scalableType = ScalableType.Content)

val localContentScale: Scale
    @Composable
    get() = Distance.local.contentScale

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
