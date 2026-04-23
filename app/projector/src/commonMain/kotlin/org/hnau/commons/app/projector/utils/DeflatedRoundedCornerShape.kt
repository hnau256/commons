package org.hnau.commons.app.projector.utils

import androidx.compose.foundation.shape.CornerSize
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection

class DeflatedRoundedCornerShape(
    private val deflation: Dp,
    private val topStart: CornerSize,
    private val topEnd: CornerSize = topStart,
    private val bottomEnd: CornerSize = topStart,
    private val bottomStart: CornerSize = topStart,
) : Shape {

    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {

        val deflation = with(density) { deflation.toPx() }

        val deflatedSize = Size(
            width = size.width - deflation * 2,
            height = size.height - deflation * 2,
        )
            .takeUnless(Size::isEmpty)
            ?: return Outline.Rectangle(Rect(0f, 0f, 0f, 0f))

        val roundRect = RoundRect(
            rect = Rect(
                left = deflation,
                top = deflation,
                right = deflatedSize.width + deflation,
                bottom = deflatedSize.height + deflation,
            ),
            topLeft = layoutDirection
                .fold(ifLtr = { topStart }, ifRtl = { topEnd })
                .toPx(size, density)
                .let(::CornerRadius),
            topRight = layoutDirection
                .fold(ifLtr = { topEnd }, ifRtl = { topStart })
                .toPx(size, density)
                .let(::CornerRadius),
            bottomRight = layoutDirection
                .fold(ifLtr = { bottomEnd }, ifRtl = { bottomStart })
                .toPx(size, density)
                .let(::CornerRadius),
            bottomLeft = layoutDirection
                .fold(ifLtr = { bottomStart }, ifRtl = { bottomEnd })
                .toPx(size, density)
                .let(::CornerRadius),
        )

        return Outline.Rounded(roundRect)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is DeflatedRoundedCornerShape) return false
        return deflation == other.deflation &&
                topStart == other.topStart &&
                topEnd == other.topEnd &&
                bottomEnd == other.bottomEnd &&
                bottomStart == other.bottomStart
    }

    override fun hashCode(): Int {
        var result = deflation.hashCode()
        result = 31 * result + topStart.hashCode()
        result = 31 * result + topEnd.hashCode()
        result = 31 * result + bottomEnd.hashCode()
        result = 31 * result + bottomStart.hashCode()
        return result
    }
}