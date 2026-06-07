package org.hnau.commons.app.projector.fractal.table.utils

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import org.hnau.commons.app.projector.fractal.table.STableScope
import org.hnau.commons.app.projector.fractal.utils.LocalShapeCorners
import org.hnau.commons.app.projector.fractal.utils.ShapeCorners
import org.hnau.commons.app.projector.uikit.line.LinePosition
import org.hnau.commons.app.projector.uikit.line.LineScope
import org.hnau.commons.app.projector.uikit.line.onPositionInLineChanged
import org.hnau.commons.app.projector.utils.Orientation
import org.hnau.commons.kotlin.Mutable

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
        val positionHolder = remember {
            Mutable(
                LinePosition(
                    isFirst = false,
                    isLast = false,
                )
            )
        }

        val cornersProvider by remember {
            derivedStateOf {
                ShapeCorners.Provider {
                    val position = positionHolder.value
                    this@STableScopeImpl.corners
                        .getTableCorners()
                        .close(
                            orientation = orientation,
                            startOrTop = !position.isFirst,
                            endOrBottom = !position.isLast,
                        )
                }
            }
        }
        Box(
            modifier = modifier.onPositionInLineChanged { newPosition ->
                positionHolder.value = newPosition
            },
            propagateMinConstraints = true,
        ) {
            CompositionLocalProvider(
                value = LocalShapeCorners provides cornersProvider,
                content = content,
            )
        }
    }
}