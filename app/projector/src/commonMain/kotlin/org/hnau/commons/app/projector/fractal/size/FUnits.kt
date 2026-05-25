package org.hnau.commons.app.projector.fractal.size

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.hnau.commons.app.projector.fractal.utils.Distance
import org.hnau.commons.app.projector.utils.DirectionValues
import org.hnau.commons.app.projector.utils.OrientationValues
import org.hnau.commons.app.projector.utils.compareWith

class FUnits private constructor(
    val padding: DirectionValues<SizeTypeValues<Dp>>,
    val cornerRadius: Dp,
    val shape: Shape,
    val borderWidth: Dp,
    val textStyle: SizeTypeValues<TextStyle>,
    val iconSize: Dp,
) {

    val paddingValues: OrientationValues<SizeTypeValues<PaddingValues>> =
        OrientationValues.create { containerOrientation ->
            SizeTypeValues.create { sizeType ->
                val values = OrientationValues.create { dimensionOrientation ->
                    val direction = dimensionOrientation.compareWith(containerOrientation)
                    padding[direction][sizeType]
                }
                PaddingValues(
                    horizontal = values.horizontal,
                    vertical = values.vertical,
                )
            }
        }

    companion object {

        private val cache = HashMap<Int, FUnits>()

        private val textStyleConfigs: SizeTypeValues<TextStyleConfig> = SizeTypeValues(
            large = TextStyleConfig(
                size = 28.sp,
                weight = FontWeight.Normal,
                letterSpacing = 0.1.sp,
                lineHeightFactor = 1.1f,
            ),
            medium = TextStyleConfig(
                size = 18.sp,
                weight = FontWeight.Medium,
                letterSpacing = 0.5.sp,
                lineHeightFactor = 1.1f,
            ),
            small = TextStyleConfig(
                size = 16.sp,
                weight = FontWeight.SemiBold,
                letterSpacing = 0.sp,
                lineHeightFactor = 1.1f,
            ),
            extraSmall = TextStyleConfig(
                size = 12.sp,
                weight = FontWeight.Bold,
                letterSpacing = 0.sp,
                lineHeightFactor = 1.1f,
            ),
        )

        operator fun get(
            distance: Distance,
        ): FUnits = cache.getOrPut(
            key = distance.distance,
        ) {
            val cornerRadius = 16.dp.scale(distance.scale.space)
            val borderWidth = 2.dp.scale(distance.scale.content, 0.25.dp)
            FUnits(
                padding = DirectionValues(
                    along = 20.dp,
                    across = 16.dp,
                )
                    .map { padding -> padding.scale(distance.scale.space) }
                    .map { medium ->
                        SizeTypeValues(
                            medium = medium,
                            large = medium * 1.5f,
                            small = medium / 1.5f,
                            extraSmall = medium / 1.5f / 1.5f,
                        )
                    },
                cornerRadius = cornerRadius,
                shape = RoundedCornerShape(size = cornerRadius),
                borderWidth = borderWidth,
                textStyle = textStyleConfigs.map { config ->
                    config.toTextStyle(distance)
                },
                iconSize = 32.dp.scale(distance.scale.content),
            )
        }

    }
}

val Distance.units: FUnits
    get() = FUnits[this]