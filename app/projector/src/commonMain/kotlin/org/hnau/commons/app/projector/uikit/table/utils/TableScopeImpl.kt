package org.hnau.commons.app.projector.uikit.table.utils

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import org.hnau.commons.app.projector.uikit.line.LinePosition
import org.hnau.commons.app.projector.uikit.line.LineScope
import org.hnau.commons.app.projector.uikit.line.onPositionInLineChanged
import org.hnau.commons.app.projector.uikit.table.TableCorners
import org.hnau.commons.app.projector.uikit.table.TableScope
import org.hnau.commons.app.projector.utils.Orientation
import org.hnau.commons.kotlin.Mutable

internal class TableScopeImpl(
    override val orientation: Orientation,
    override val separation: Dp,
    override val corners: TableCorners.Provider,
    private val lineScope: LineScope,
) : TableScope, LineScope by lineScope {

    @Composable
    override fun Cell(
        modifier: Modifier,
        content: @Composable (TableCorners.Provider.() -> Unit),
    ) {
        val positionHolder = remember {
            Mutable(
                LinePosition(
                    isFirst = false,
                    isLast = false,
                )
            )
        }
        val corners by remember {
            derivedStateOf {
                TableCorners.Provider {
                    val position = positionHolder.value
                    corners
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
            modifier = modifier
                .onPositionInLineChanged { newPosition ->
                    positionHolder.value = newPosition
                },
            propagateMinConstraints = true,
        ) {
            content(
                corners,
            )
        }
    }
}