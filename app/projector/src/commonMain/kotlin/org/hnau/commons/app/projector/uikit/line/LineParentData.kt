package org.hnau.commons.app.projector.uikit.line

import androidx.compose.ui.geometry.Rect

internal data class LineParentData(
    var weight: Float,
    var onPositionCallback: ((LinePosition) -> Unit)?,
    var onRectCallback: ((Rect) -> Unit)?,
) {

    companion object {

        fun createEmpty(): LineParentData = LineParentData(
            weight = 0f,
            onPositionCallback = null,
            onRectCallback = null,
        )
    }
}