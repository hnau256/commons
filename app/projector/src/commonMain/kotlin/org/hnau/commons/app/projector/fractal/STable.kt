package org.hnau.commons.app.projector.fractal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.util.fastForEachIndexed
import org.hnau.commons.app.projector.fractal.context.LocalFContext
import org.hnau.commons.app.projector.fractal.context.UpdateFContext
import org.hnau.commons.app.projector.fractal.context.containerColor
import org.hnau.commons.app.projector.fractal.size.units
import org.hnau.commons.app.projector.fractal.utils.Mood
import org.hnau.commons.app.projector.fractal.utils.Saturation
import org.hnau.commons.app.projector.utils.Orientation
import org.hnau.commons.kotlin.foldBoolean

data class Column<R>(
    val saturation: Saturation? = null,
    val cell: @Composable (R) -> Unit,
)

@Composable
fun <R> STable(
    rows: List<R>,
    columns: List<Column<R>>,
    modifier: Modifier = Modifier,
    rowToFill: ForceFill? = null,
    columnToFill: ForceFill? = null,
) {
    UpdateFContext(
        update = {
            copy(
                mood = Mood.Primary,
            )
        }
    ) {
        val separation = LocalFContext.current.distance.units.borderWidth
        SLine(
            orientation = Orientation.Horizontal,
            modifier = modifier,
            separation = separation,
            forceFill = columnToFill,
        ) {
            columns.fastForEachIndexed { columnIndex, column ->
                val startIsOpened = columnIndex == 0
                val endIsOpened = columnIndex == columns.lastIndex
                SLine(
                    orientation = Orientation.Vertical,
                    separation = separation,
                    forceFill = rowToFill,
                ) {
                    rows.fastForEachIndexed { rowIndex, row ->
                        val topIsOpened = rowIndex == 0
                        val bottomIsOpened = rowIndex == rows.lastIndex
                        Box(
                            propagateMinConstraints = true,
                        ) {
                            UpdateFContext(
                                update = {
                                    copy(
                                        saturation = column.saturation ?: saturation,
                                        customContainerTone = null,
                                    )
                                }
                            ) {
                                val fContext = LocalFContext.current
                                val units = fContext.distance.units
                                val maxCornerRadius = units.cornerRadius
                                val minCornerRadius = maxCornerRadius / 2

                                val shape = RoundedCornerShape(
                                    topStart = (topIsOpened && startIsOpened).foldBoolean(
                                        { maxCornerRadius },
                                        { minCornerRadius }),
                                    topEnd = (topIsOpened && endIsOpened).foldBoolean(
                                        { maxCornerRadius },
                                        { minCornerRadius }),
                                    bottomStart = (bottomIsOpened && startIsOpened).foldBoolean(
                                        { maxCornerRadius },
                                        { minCornerRadius }),
                                    bottomEnd = (bottomIsOpened && endIsOpened).foldBoolean(
                                        { maxCornerRadius },
                                        { minCornerRadius }),
                                )

                                Box(
                                    modifier = Modifier
                                        .clip(shape)
                                        .background(fContext.containerColor)
                                        .padding(units.paddingValues.horizontal.medium),
                                    propagateMinConstraints = true,
                                ) {
                                    column.cell(row)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
