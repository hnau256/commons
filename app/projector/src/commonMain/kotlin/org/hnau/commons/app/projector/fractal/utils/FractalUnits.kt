package org.hnau.commons.app.projector.fractal.utils

import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.hnau.commons.app.projector.utils.DeflatedRoundedCornerShape
import kotlin.math.roundToInt

class FractalUnits private constructor(
    val paddingHorizontal: Dp,
    val paddingVertical: Dp,
    val shape: Shape,
    val borderShape: Shape,
    val borderWidth: Dp,
    val textStyle: TextStyle,
    val iconSize: Dp,
) {

    companion object {

        private val cache = HashMap<Int, FractalUnits>()

        private val spaceScaleDecay = BaseWithDecay.float(
            base = 1f,
            decay = 0.5,
        )

        private val contentScaleDecay = BaseWithDecay.float(
            base = 1f,
            decay = 0.75,
        )

        private val weightDecay = BaseWithDecay(
            base = FontWeight.Normal,
            decay = 1.3,
            times = { value, factor ->
                value
                    .weight
                    .times(factor)
                    .toInt()
                    .let(::FontWeight)
            },
        )

        private val letterSpacingDecay = BaseWithDecay.textUnit(
            base = 0.5.sp,
            decay = 1.4,
            baseline = 0.sp,
        )

        operator fun get(distance: Distance): FractalUnits =
            cache.getOrPut(distance.distance) {
                val spaceScale = spaceScaleDecay[distance]
                val contentScale = contentScaleDecay[distance]
                val weight = weightDecay[distance].coerceIn(FontWeight.Thin, FontWeight.Black)
                val letterSpacing = letterSpacingDecay[distance]
                val cornerRadius = scale(8.dp, spaceScale)
                val borderWidth = scale(1.5.dp, contentScale, 0.25.dp)
                FractalUnits(
                    paddingHorizontal = scale(16.dp, spaceScale),
                    paddingVertical = scale(8.dp, spaceScale),
                    shape = RoundedCornerShape(size = cornerRadius),
                    borderShape = DeflatedRoundedCornerShape(
                        topStart = CornerSize(cornerRadius),
                        deflation = borderWidth / 2,
                    ),
                    borderWidth = borderWidth,
                    textStyle = TextStyle(
                        fontSize = 20.sp * contentScale,
                        fontWeight = weight,
                        letterSpacing = letterSpacing,
                    ),
                    iconSize = scale(24.dp, contentScale),
                )
            }

        private fun scale(
            value: Dp,
            scale: Float,
            step: Dp = 4.dp,
        ): Dp = value.value
            .times(scale)
            .div(step.value)
            .roundToInt()
            .times(step.value)
            .dp
            .coerceAtLeast(step)
    }
}

val Distance.units: FractalUnits
    get() = FractalUnits[this]

val localUnits: FractalUnits
    @Composable
    get() = Distance.local.units