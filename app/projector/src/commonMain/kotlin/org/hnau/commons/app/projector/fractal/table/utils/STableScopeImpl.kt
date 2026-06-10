package org.hnau.commons.app.projector.fractal.table.utils

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import org.hnau.commons.app.projector.fractal.table.STableScope
import org.hnau.commons.app.projector.fractal.utils.LocalShapeCorners
import org.hnau.commons.app.projector.fractal.utils.ShapeCorners
import org.hnau.commons.app.projector.uikit.line.LinePosition
import org.hnau.commons.app.projector.uikit.line.LineScope
import org.hnau.commons.app.projector.uikit.line.onPositionInLineChanged
import org.hnau.commons.app.projector.utils.Orientation

internal class STableScopeImpl(
    override val orientation: Orientation,
    private val corners: ShapeCorners.Provider,
    private val lineScope: LineScope,
) : STableScope, LineScope by lineScope {

    @Composable
    override fun SCell(
        modifier: Modifier,
        content: @Composable () -> Unit,
    ) {
        var position by remember { mutableStateOf(LinePosition(false, false)) }
        val tableCorners = this.corners

        val cornersProvider = remember(tableCorners) {
            ShapeCorners.Provider {
                val pos = position
                tableCorners
                    .getTableCorners()
                    .close(
                        orientation = orientation,
                        startOrTop = !pos.isFirst,
                        endOrBottom = !pos.isLast,
                    )
            }
        }
        Box(
            modifier = modifier.onPositionInLineChanged { position = it },
            propagateMinConstraints = true,
        ) {
            CompositionLocalProvider(
                value = LocalShapeCorners provides cornersProvider,
                content = content,
            )
        }
    }
}