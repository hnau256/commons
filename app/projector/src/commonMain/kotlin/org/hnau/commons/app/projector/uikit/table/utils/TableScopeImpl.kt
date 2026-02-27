package org.hnau.commons.app.projector.uikit.table.utils

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.hnau.commons.app.projector.uikit.table.TableCorners
import org.hnau.commons.app.projector.uikit.table.TableOrientation
import org.hnau.commons.app.projector.uikit.table.TableScope

internal class TableScopeImpl(
    override val orientation: TableOrientation,
    override val corners: TableCorners,
    private val applyWeight: Modifier.(weight: Float, fill: Boolean) -> Modifier,
) : TableScope {

    @Composable
    override fun Cell(
        content: @Composable (TableCorners.(Modifier) -> Unit),
    ) {
        content(
            TableCorners.opened,
            orientation.fold(
                ifHorizontal = { Modifier.fillMaxHeight() },
                ifVertical = { Modifier.fillMaxWidth() },
            ),
        )
    }

    override fun Modifier.weight(
        weight: Float,
        fill: Boolean,
    ): Modifier = applyWeight(weight, fill)
}

@Composable
internal fun TableScope.Companion.create(
    orientation: TableOrientation,
    corners: TableCorners,
    columnScope: ColumnScope,
): TableScopeImpl = TableScopeImpl(
    orientation = orientation,
    corners = corners,
) { weight, fill ->
    with(columnScope) {
        weight(
            weight = weight,
            fill = fill,
        )
    }
}

@Composable
internal fun TableScope.Companion.create(
    orientation: TableOrientation,
    corners: TableCorners,
    rowScope: RowScope,
): TableScopeImpl = TableScopeImpl(
    orientation = orientation,
    corners = corners,
) { weight, fill ->
    with(rowScope) {
        weight(
            weight = weight,
            fill = fill,
        )
    }
}