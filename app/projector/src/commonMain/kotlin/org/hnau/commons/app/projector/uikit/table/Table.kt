package org.hnau.commons.app.projector.uikit.table

import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonSkippableComposable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import org.hnau.commons.app.projector.uikit.line.Line
import org.hnau.commons.app.projector.uikit.line.LineScope
import org.hnau.commons.app.projector.uikit.table.utils.TableScopeImpl
import org.hnau.commons.app.projector.utils.Orientation


@Composable
@NonSkippableComposable
fun Table(
    orientation: Orientation,
    modifier: Modifier = Modifier,
    config: TableConfig = TableConfig.default,
    corners: TableCorners.Provider = TableCorners.Provider.opened,
    content: @Composable TableScope.() -> Unit,
) {
    Line(
        modifier = modifier,
        orientation = orientation,
        separation = config.separation,
    ) {
        val lineScope: LineScope = this
        val scope = remember(
            orientation,
            corners,
            lineScope
        ) {
            TableScopeImpl(
                orientation = orientation,
                corners = corners,
                tableConfig = config,
                lineScope = lineScope,
            )
        }
        scope.content()
    }
}