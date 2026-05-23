package org.hnau.commons.app.projector.fractal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import org.hnau.commons.app.projector.fractal.context.LocalFContext
import org.hnau.commons.app.projector.fractal.context.UpdateFContext
import org.hnau.commons.app.projector.fractal.context.containerColor
import org.hnau.commons.app.projector.fractal.size.units
import org.hnau.commons.app.projector.fractal.utils.Saturation
import org.hnau.commons.app.projector.fractal.utils.plus
import org.hnau.commons.app.projector.uikit.table.Table
import org.hnau.commons.app.projector.uikit.table.TableCorners
import org.hnau.commons.app.projector.uikit.table.TableScope
import org.hnau.commons.app.projector.uikit.table.rememberCellShape
import org.hnau.commons.app.projector.uikit.utils.Dimens
import org.hnau.commons.app.projector.utils.Orientation

@Composable
fun STable(
    orientation: Orientation,
    modifier: Modifier = Modifier,
    corners: TableCorners.Provider = TableCorners.Provider.opened,
    reverseOrdering: Boolean = false,
    content: @Composable TableScope.() -> Unit,
) {
    Table(
        orientation = orientation,
        modifier = modifier,
        separation = LocalFContext.current.distance.units.borderWidth,
        corners = corners,
        reverseOrdering = reverseOrdering,
        content = content,
    )
}

@Composable
fun TableScope.SCell(
    content: @Composable TableCorners.Provider.(Modifier, Shape) -> Unit,
) {
    Cell { modifier ->
        val shape = rememberSCellShape()
        UpdateFContext(
            update = {
                copy(
                    distance = distance + 1,
                    saturation = Saturation.Neutral,
                    customContainerTone = null,
                )
            }
        ) {
            content(modifier, shape)
        }
    }
}

@Composable
private fun TableCorners.Provider.rememberSCellShape(): Shape = rememberCellShape(
    cornerRadii = LocalFContext.current.distance.units.cornerRadius.let { max ->
        (max / 3)..max
    }
)

@Composable
fun TableScope.SCellBox(
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.Center,
    contentOrientation: Orientation = Orientation.Horizontal,
    propagateMinConstraints: Boolean = false,
    content: @Composable BoxScope.() -> Unit,
) {
    SCell { cellModifier, shape ->
        val fContext = LocalFContext.current
        Box(
            modifier = cellModifier
                .then(modifier)
                .background(
                    color = fContext.containerColor,
                    shape = shape,
                )
                .padding(
                    fContext.distance.units.paddingValues[contentOrientation].medium,
                ),
            contentAlignment = contentAlignment,
            propagateMinConstraints = propagateMinConstraints,
        ) {
            content()
        }
    }
}