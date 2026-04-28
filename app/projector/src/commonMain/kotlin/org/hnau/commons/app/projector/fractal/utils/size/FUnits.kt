package org.hnau.commons.app.projector.fractal.utils.size

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.hnau.commons.app.projector.fractal.utils.BaseWithDecay
import org.hnau.commons.app.projector.fractal.utils.Distance
import org.hnau.commons.app.projector.fractal.utils.fontWeight
import org.hnau.commons.app.projector.fractal.utils.local
import org.hnau.commons.app.projector.fractal.utils.textUnit
import org.hnau.commons.app.projector.utils.DeflatedRoundedCornerShape

class FUnits private constructor(
    val horizontal: Spaces,
    val vertical: Spaces,
    val shape: Shape,
    val borderShape: Shape,
    val borderWidth: Dp,
    val textStyle: TextStyleValues<TextStyle>,
    val iconSize: Dp,
) {

    companion object {

        private val cache = HashMap<Int, FUnits>()

        private val textStyleConfigs: TextStyleValues<TextStyleConfig> = TextStyleValues(
            title = TextStyleConfig(
                size = 22.sp,
                weight = FontWeight.Normal,
                letterSpacing = 0.1.sp,
                lineHeightFactor = 1.1f,
            ),
            default = TextStyleConfig(
                size = 16.sp,
                weight = FontWeight.Medium,
                letterSpacing = 0.5.sp,
                lineHeightFactor = 1.1f,
            ),
            label = TextStyleConfig(
                size = 14.sp,
                weight = FontWeight.SemiBold,
                letterSpacing = 0.sp,
                lineHeightFactor = 1.1f,
            ),
        )

        operator fun get(
            distance: Distance,
        ): FUnits = cache.getOrPut(
            key = distance.distance,
        ) {
            val cornerRadius = 12.dp.scale(distance.scale.space)
            val borderWidth = 2.dp.scale(distance.scale.content, 0.25.dp)
            FUnits(
                horizontal = Spaces(
                    medium = 16.dp.scale(distance.scale.space),
                ),
                vertical = Spaces(
                    medium = 8.dp.scale(distance.scale.space),
                ),
                shape = RoundedCornerShape(size = cornerRadius),
                borderShape = DeflatedRoundedCornerShape(
                    topStart = CornerSize(cornerRadius),
                    deflation = borderWidth / 2,
                ),
                borderWidth = borderWidth,
                textStyle = textStyleConfigs.map { config ->
                    config.toTextStyle(distance)
                },
                iconSize = 32.dp.scale(distance.scale.content),
            )
        }

        val local: FUnits
            @Composable
            get() = Distance.local.units
    }
}

val Distance.units: FUnits
    get() = FUnits[this]

@Composable
fun Modifier.fPadding(): Modifier = padding(
    horizontal = FUnits.local.horizontal.medium,
    vertical = FUnits.local.vertical.medium,
)