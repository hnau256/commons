package org.hnau.commons.app.projector.uikit.table

import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.toRect
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import org.hnau.commons.app.projector.utils.rememberLet
import org.hnau.commons.kotlin.foldBoolean

val CellScope.shape: Shape
    @Composable
    get() = rememberLet { cellScope ->
        CellShape(cellScope = cellScope)
    }

private class CellShape(
    private val cellScope: CellScope,
) : Shape {

    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {

        val corners = cellScope.corners.getTableCorners()

        val radius: (Boolean) -> CornerRadius = { isOpened ->
            CornerRadius(
                with(density) {
                    isOpened.foldBoolean(
                        ifTrue = { cellScope.cornerRadius.endInclusive },
                        ifFalse = { cellScope.cornerRadius.start },
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