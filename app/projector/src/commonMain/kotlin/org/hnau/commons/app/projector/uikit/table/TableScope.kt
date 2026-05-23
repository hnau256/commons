package org.hnau.commons.app.projector.uikit.table

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import org.hnau.commons.app.projector.uikit.line.LineScope
import org.hnau.commons.app.projector.utils.Orientation

interface TableScope : LineScope {

    val orientation: Orientation

    val separation: Dp

    val corners: TableCorners.Provider

    @Composable
    fun Cell(
        content: @Composable CellScope.(Modifier) -> Unit,
    )

    companion object
}