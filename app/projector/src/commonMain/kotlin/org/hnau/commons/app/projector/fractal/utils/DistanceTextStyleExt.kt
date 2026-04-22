package org.hnau.commons.app.projector.fractal.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

private val weight: BaseWithDecay<FontWeight> = BaseWithDecay(
    base = FontWeight.Normal,
    decay = 1.0,
    times = { value, factor ->
        value
            .weight
            .times(factor)
            .toInt()
            .let(::FontWeight)
    },
)

private val letterSpacing: BaseWithDecay<TextUnit> = BaseWithDecay.textUnit(
    base = 0.5.sp,
    decay = 1.02,
)

val Distance.textStyle: TextStyle
    get() = TextStyle(
        fontSize = 20.sp * contentScale.scale,
        fontWeight = weight[this],
        letterSpacing = letterSpacing[this],
    )

val localTextStyle: TextStyle
    @Composable
    get() = Distance.local.textStyle