package org.hnau.commons.app.projector.uikit.table

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonSkippableComposable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import org.hnau.commons.app.projector.uikit.line.Line
import org.hnau.commons.app.projector.uikit.line.LineScope
import org.hnau.commons.app.projector.uikit.table.utils.TableScopeImpl
import org.hnau.commons.app.projector.uikit.utils.Dimens
import org.hnau.commons.app.projector.utils.Orientation

private val arrangement = Arrangement.spacedBy(Dimens.chipsSeparation)

@Composable
@NonSkippableComposable
fun Table(
    orientation: Orientation,
    modifier: Modifier = Modifier,
    corners: TableCorners.Provider = TableCorners.Provider.opened,
    content: @Composable TableScope.() -> Unit,
) {
    Line(
        modifier = modifier,
        orientation = orientation,
        arrangement = arrangement,
    ) {
        val lineScope: LineScope = this
        val scope = remember(
            orientation,
            corners,
            lineScope
        ) {
            TableScopeImpl(
                orientation, corners, lineScope
            )
        }
        scope.content()
    }
}