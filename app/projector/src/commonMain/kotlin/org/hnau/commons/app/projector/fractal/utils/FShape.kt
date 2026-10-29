package org.hnau.commons.app.projector.fractal.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.toRect
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import org.hnau.commons.app.projector.fractal.distance.LocalDistance
import org.hnau.commons.app.projector.fractal.size.units
import org.hnau.commons.kotlin.foldBoolean

internal const val ClosedCornerRadiusDivider: Float = 3f

@Composable
fun rememberFShape(
    corners: ShapeCorners.Provider = LocalShapeCorners.current,
    cornerRadius: Dp = LocalDistance.current.units.cornerRadius,
): Shape = remember(corners, cornerRadius) {
    DynamicFShape(
        corners = corners,
        cornerRadius = cornerRadius,
    )
}

internal data class FShapeCorners(
    val topLeft: Dp,
    val topRight: Dp,
    val bottomLeft: Dp,
    val bottomRight: Dp,
) {

    fun lerp(
        other: FShapeCorners,
        fraction: Float,
    ): FShapeCorners = FShapeCorners(
        topLeft = topLeft.lerp(other.topLeft, fraction),
        topRight = topRight.lerp(other.topRight, fraction),
        bottomLeft = bottomLeft.lerp(other.bottomLeft, fraction),
        bottomRight = bottomRight.lerp(other.bottomRight, fraction),
    )
}

internal class FShape(
    private val corners: FShapeCorners,
) : Shape {

    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density,
    ): Outline = Outline.Rounded(
        RoundRect(
            rect = size.toRect(),
            topLeft = corners.topLeft.toCornerRadius(density),
            topRight = corners.topRight.toCornerRadius(density),
            bottomLeft = corners.bottomLeft.toCornerRadius(density),
            bottomRight = corners.bottomRight.toCornerRadius(density),
        ),
    )

    fun lerp(
        other: FShape,
        fraction: Float,
    ): FShape = FShape(
        corners = corners.lerp(other.corners, fraction),
    )

    fun toRoundRect(
        rect: Rect,
        density: Density,
    ): RoundRect = RoundRect(
        rect = rect,
        topLeft = corners.topLeft.toCornerRadius(density),
        topRight = corners.topRight.toCornerRadius(density),
        bottomLeft = corners.bottomLeft.toCornerRadius(density),
        bottomRight = corners.bottomRight.toCornerRadius(density),
    )
}

private class DynamicFShape(
    private val corners: ShapeCorners.Provider,
    private val cornerRadius: Dp,
) : Shape {

    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density,
    ): Outline = corners
        .getTableCorners()
        .toFShape(
            cornerRadius = cornerRadius,
            layoutDirection = layoutDirection,
        )
        .createOutline(
            size = size,
            layoutDirection = layoutDirection,
            density = density,
        )
}

internal fun ShapeCorners.toFShape(
    cornerRadius: Dp,
    layoutDirection: LayoutDirection,
): FShape {

    val radius: (Boolean) -> Dp = { opened ->
        opened.foldBoolean(
            ifTrue = { cornerRadius },
            ifFalse = { cornerRadius / ClosedCornerRadiusDivider },
        )
    }

    val startTop = radius(startTopIsOpened)
    val startBottom = radius(startBottomIsOpened)
    val endTop = radius(endTopIsOpened)
    val endBottom = radius(endBottomIsOpened)

    return FShape(
        corners = when (layoutDirection) {
            LayoutDirection.Ltr -> FShapeCorners(
                topLeft = startTop,
                topRight = endTop,
                bottomLeft = startBottom,
                bottomRight = endBottom,
            )

            LayoutDirection.Rtl -> FShapeCorners(
                topLeft = endTop,
                topRight = startTop,
                bottomLeft = endBottom,
                bottomRight = startBottom,
            )
        },
    )
}

private fun Dp.lerp(
    other: Dp,
    fraction: Float,
): Dp = (value + (other.value - value) * fraction).dp

private fun Dp.toCornerRadius(
    density: Density,
): CornerRadius = with(density) {
    CornerRadius(toPx())
}
