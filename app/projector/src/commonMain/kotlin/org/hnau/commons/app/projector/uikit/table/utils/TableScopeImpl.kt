package org.hnau.commons.app.projector.uikit.table.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import org.hnau.commons.app.projector.uikit.line.LinePosition
import org.hnau.commons.app.projector.uikit.line.LineScope
import org.hnau.commons.app.projector.uikit.line.onPositionInLineChanged
import org.hnau.commons.app.projector.uikit.table.TableCorners
import org.hnau.commons.app.projector.uikit.table.TableScope
import org.hnau.commons.app.projector.utils.Orientation

internal class TableScopeImpl(
    override val orientation: Orientation,
    override val separation: Dp,
    override val corners: TableCorners.Provider,
    private val lineScope: LineScope,
) : TableScope, LineScope by lineScope {

    @Composable
    override fun Cell(
        content: @Composable (TableCorners.Provider.(Modifier) -> Unit),
    ) {
        var position by remember {
            mutableStateOf(
                LinePosition(
                    isFirst = false,
                    isLast = false,
                )
            )
        }
        val corners by remember {
            derivedStateOf {
                TableCorners.Provider {
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
        content(
            corners,
            Modifier.onPositionInLineChanged { newPosition ->
                position = newPosition
            },
        )
    }
}