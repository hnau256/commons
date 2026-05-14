package org.hnau.commons.app.projector.fractal.size

import androidx.compose.foundation.layout.PaddingValues
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
import org.hnau.commons.app.projector.fractal.context.LocalFContext
import org.hnau.commons.app.projector.fractal.utils.Distance
import org.hnau.commons.app.projector.utils.DeflatedRoundedCornerShape
import org.hnau.commons.app.projector.utils.Orientation
import org.hnau.commons.app.projector.utils.OrientationValues

class FUnits private constructor(
    val padding: OrientationValues<SizeTypeValues<Dp>>,
    val cornerRadius: Dp,
    val shape: Shape,
    val borderShape: Shape,
    val borderWidth: Dp,
    val textStyle: SizeTypeValues<TextStyle>,
    val iconSize: Dp,
) {

    val paddingValues: SizeTypeValues<PaddingValues> = SizeTypeValues.create { sizeType ->
        PaddingValues(
            horizontal = padding[Orientation.Horizontal][sizeType],
            vertical = padding[Orientation.Vertical][sizeType],
        )
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
                size = 16.sp,
                weight = FontWeight.Medium,
                letterSpacing = 0.5.sp,
                lineHeightFactor = 1.1f,
            ),
            small = TextStyleConfig(
                size = 12.sp,
                weight = FontWeight.SemiBold,
                letterSpacing = 0.sp,
                lineHeightFactor = 1.1f,
            ),
            extraSmall = TextStyleConfig(
                size = 8.sp,
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
            val cornerRadius = 12.dp.scale(distance.scale.space)
            val borderWidth = 2.dp.scale(distance.scale.content, 0.25.dp)
            FUnits(
                padding = OrientationValues(
                    horizontal = 20.dp,
                    vertical = 12.dp,
                ).map { medium ->
                    SizeTypeValues(
                        medium = medium,
                        large = medium * 2,
                        small = medium / 2,
                        extraSmall = medium / 4,
                    )
                },
                cornerRadius = cornerRadius,
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

    }
}

val Distance.units: FUnits
    get() = FUnits[this]

@Composable
fun Modifier.fPadding(
    spaceSize: SizeType = SizeType.default,
): Modifier = padding(
    paddingValues = LocalFContext.current.distance.units.paddingValues[spaceSize]
)