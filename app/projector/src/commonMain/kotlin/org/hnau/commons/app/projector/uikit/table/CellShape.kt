package org.hnau.commons.app.projector.uikit.table

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.toRect
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import org.hnau.commons.app.projector.uikit.utils.Dimens
import org.hnau.commons.app.projector.utils.rememberLet
import org.hnau.commons.app.projector.utils.rememberRun
import org.hnau.commons.kotlin.foldBoolean

val TableCorners.Provider.shape: Shape
    @Composable
    get() = rememberLet { cornersProvider ->
        CellShape(tableCorners = cornersProvider)
    }

private class CellShape(
    private val tableCorners: TableCorners.Provider,
) : Shape {

    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {

        val corners = tableCorners.getTableCorners()

        val radius: (Boolean) -> CornerRadius = { isOpened ->
            CornerRadius(
                with(density) {
                    isOpened.foldBoolean(
                        ifTrue = { Dimens.cornerRadius },
                        ifFalse = { Dimens.cornerRadiusMin },
                    ).toPx()
                }
            )
        }

        val leftTopRadius = radius(
            when (layoutDirection) {
                LayoutDirection.Ltr -> corners.startTopIsOpened
                LayoutDirection.Rtl -> corners.endTopIsOpened
            },
        )

        val rightTopRadius = radius(
            when (layoutDirection) {
                LayoutDirection.Ltr -> corners.endTopIsOpened
                LayoutDirection.Rtl -> corners.startTopIsOpened
            },
        )

        val leftBottomRadius = radius(
            when (layoutDirection) {
                LayoutDirection.Ltr -> corners.startBottomIsOpened
                LayoutDirection.Rtl -> corners.endBottomIsOpened
            },
        )

        val rightBottomRadius = radius(
            when (layoutDirection) {
                LayoutDirection.Ltr -> corners.endBottomIsOpened
                LayoutDirection.Rtl -> corners.startBottomIsOpened
            },
        )

        return Outline.Rounded(
            RoundRect(
                rect = size.toRect(),
                topLeft = leftTopRadius,
                topRight = rightTopRadius,
                bottomLeft = leftBottomRadius,
                bottomRight = rightBottomRadius,
            ),
        )
    }
}