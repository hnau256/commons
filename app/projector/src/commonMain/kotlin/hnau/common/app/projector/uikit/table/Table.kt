package hnau.common.app.projector.uikit.table

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonSkippableComposable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import hnau.common.app.projector.uikit.table.utils.create
import hnau.common.app.projector.uikit.utils.Dimens

private val arrangement = Arrangement.spacedBy(Dimens.chipsSeparation)

@Deprecated("Do not use Table")
@Composable
@NonSkippableComposable
fun Table(
    orientation: TableOrientation,
    modifier: Modifier = Modifier,
    corners: TableCorners = TableCorners.opened,
    content: @Composable TableScope.() -> Unit,
) {
    orientation.fold(
        ifHorizontal = {
            Row(
                modifier = modifier
                    .height(IntrinsicSize.Max),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = arrangement,
            ) {
                TableScope
                    .create(
                        orientation = orientation,
                        corners = corners,
                        rowScope = this,
                    ).apply {
                        content()
                    }
            }
        },
        ifVertical = {
            Column(
                modifier = modifier
                    .width(IntrinsicSize.Max),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = arrangement,
            ) {
                TableScope
                    .create(
                        orientation = orientation,
                        corners = corners,
                        columnScope = this,
                    ).apply {
                        content()
                    }
            }
        },
    )
}