package org.hnau.commons.app.projector.fractal.table.utils

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import org.hnau.commons.app.projector.fractal.context.LocalFContext
import org.hnau.commons.app.projector.fractal.context.UpdateFContext
import org.hnau.commons.app.projector.fractal.size.units
import org.hnau.commons.app.projector.uikit.line.LinePosition
import org.hnau.commons.app.projector.uikit.line.LineScope
import org.hnau.commons.app.projector.uikit.line.onPositionInLineChanged
import org.hnau.commons.app.projector.fractal.table.SCellScope
import org.hnau.commons.app.projector.fractal.table.SCellShape
import org.hnau.commons.app.projector.fractal.table.STableCorners
import org.hnau.commons.app.projector.fractal.table.STableScope
import org.hnau.commons.app.projector.fractal.utils.Saturation
import org.hnau.commons.app.projector.fractal.utils.plus
import org.hnau.commons.app.projector.utils.Orientation
import org.hnau.commons.kotlin.Mutable

internal class STableScopeImpl(
    override val orientation: Orientation,
    override val corners: STableCorners.Provider,
    private val lineScope: LineScope,
) : STableScope, LineScope by lineScope {

    @Composable
    override fun SCell(
        modifier: Modifier,
        content: @Composable SCellScope.() -> Unit,
    ) {
        val positionHolder = remember {
            Mutable(
                LinePosition(
                    isFirst = false,
                    isLast = false,
                )
            )
        }
        val cornerRadius = LocalFContext.current.distance.units.cornerRadius
        val sCellScope by remember {
            derivedStateOf {
                object : SCellScope {

                    override val corners: STableCorners.Provider = STableCorners.Provider {
                        val position = positionHolder.value
                        this@STableScopeImpl.corners
                            .getTableCorners()
                            .close(
                                orientation = orientation,
                                startOrTop = !position.isFirst,
                                endOrBottom = !position.isLast,
                            )
                    }

                    override val shape: Shape = SCellShape(
                        tableCorners = corners,
                        cornerRadii = (cornerRadius / 3)..cornerRadius,
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
            UpdateFContext(
                update = {
                    copy(
                        distance = distance + 1,
                        saturation = Saturation.Neutral,
                        customContainerTone = null,
                    )
                }
            ) {
                sCellScope.content()
            }
        }
    }
}