package org.hnau.commons.app.projector.uikit.line

internal data class LineParentData(
    var weight: Float,
    var onPositionCallback: ((LinePosition) -> Unit)?,
) {

    companion object {

        fun createEmpty(): LineParentData = LineParentData(
            weight = 0f,
            onPositionCallback = null,
        )
    }
}