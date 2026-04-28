package org.hnau.commons.app.projector.uikit.table

import androidx.annotation.FloatRange
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.hnau.commons.app.projector.utils.Orientation

interface TableScope {

    val orientation: Orientation

    val corners: TableCorners

    @Composable
    fun Cell(
        content: @Composable TableCorners.(Modifier) -> Unit,
    )

    fun Modifier.weight(
        @FloatRange(from = 0.0, fromInclusive = false) weight: Float,
        fill: Boolean = true,
    ): Modifier

    companion object
}