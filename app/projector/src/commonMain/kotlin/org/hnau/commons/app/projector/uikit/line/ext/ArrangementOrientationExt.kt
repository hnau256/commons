package org.hnau.commons.app.projector.uikit.line.ext

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import org.hnau.commons.app.projector.utils.Orientation
import org.hnau.commons.app.projector.utils.fold

context(orientation: Orientation, arrangement: Arrangement.HorizontalOrVertical)
fun Density.arrangeForCorrectOrientation(
    totalSize: Int,
    sizes: IntArray,
    layoutDirection: LayoutDirection,
    outPositions: IntArray,
) {
    with(arrangement) {
        orientation.fold(
            ifHorizontal = {
                arrange(
                    totalSize = totalSize,
                    sizes = sizes,
                    outPositions = outPositions,
                    layoutDirection = layoutDirection,
                )
            },
            ifVertical = {
                arrange(
                    totalSize = totalSize,
                    sizes = sizes,
                    outPositions = outPositions,
                )
            },
        )
    }
}