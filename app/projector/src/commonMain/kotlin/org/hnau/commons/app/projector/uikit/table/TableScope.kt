package org.hnau.commons.app.projector.uikit.table

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.hnau.commons.app.projector.uikit.line.LineScope
import org.hnau.commons.app.projector.utils.Orientation

interface TableScope : LineScope {

    val orientation: Orientation

    val corners: TableCorners.Provider

    @Composable
    fun Cell(
        content: @Composable TableCorners.Provider.(Modifier) -> Unit,
    )

    companion object
}