package org.hnau.commons.app.projector.uikit.table

import androidx.compose.ui.unit.Dp

data class CellScope(
    val corners: TableCorners.Provider,
    val cornerRadius: ClosedRange<Dp>,
)