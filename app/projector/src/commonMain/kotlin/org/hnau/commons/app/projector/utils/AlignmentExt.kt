package org.hnau.commons.app.projector.utils

import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection


val Alignment.opposite: Alignment
    get() = when (this) {
        Alignment.TopStart -> Alignment.BottomEnd
        Alignment.TopCenter -> Alignment.BottomCenter
        Alignment.TopEnd -> Alignment.BottomStart
        Alignment.CenterStart -> Alignment.CenterEnd
        Alignment.Center -> Alignment.Center
        Alignment.CenterEnd -> Alignment.CenterStart
        Alignment.BottomStart -> Alignment.TopEnd
        Alignment.BottomCenter -> Alignment.TopCenter
        Alignment.BottomEnd -> Alignment.TopStart
        else -> OppositeAlignment(this)
    }

private class OppositeAlignment(
    private val source: Alignment,
) : Alignment {

    override fun align(
        size: IntSize,
        space: IntSize,
        layoutDirection: LayoutDirection
    ): IntOffset {
        val sourceOffset = source.align(size, space, layoutDirection)
        return IntOffset(
            x = space.width - size.width - sourceOffset.x,
            y = space.height - size.height - sourceOffset.y
        )
    }
}
