package org.hnau.commons.app.projector.uikit.line

internal data class LineParentData(
    var weight: Float,
) {

    companion object {

        fun createEmpty(): LineParentData = LineParentData(
            weight = 0f,
        )
    }
}