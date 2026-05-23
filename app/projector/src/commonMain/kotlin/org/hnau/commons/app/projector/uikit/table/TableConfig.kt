package org.hnau.commons.app.projector.uikit.table

import androidx.compose.ui.unit.Dp
import org.hnau.commons.app.projector.uikit.utils.Dimens

data class TableConfig(
    val separation: Dp,
    val cornerRadius: ClosedRange<Dp>,
) {

    companion object {

        val default = TableConfig(
            separation = Dimens.chipsSeparation,
            cornerRadius = Dimens.cornerRadiusMin..Dimens.cornerRadius,
        )
    }
}