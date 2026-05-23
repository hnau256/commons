package org.hnau.commons.app.projector.uikit.table

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import org.hnau.commons.app.projector.utils.opposite

@Composable
fun TableScope.Subtable(
    modifier: Modifier = Modifier,
    reverseOrdering: Boolean = false,
    content: @Composable TableScope.() -> Unit,
) {
    Cell { cellModifier ->
        Table(
            modifier = cellModifier.then(modifier),
            orientation = orientation.opposite,
            corners = this,
            content = content,
            separation = separation,
            reverseOrdering = reverseOrdering,
        )
    }
}

@Composable
fun TableScope.CellBox(
    modifier: Modifier = Modifier,
    backgroundColor: Color = TableDefaults.cellColor,
    contentAlignment: Alignment = Alignment.Center,
    propagateMinConstraints: Boolean = false,
    content: @Composable BoxScope.(Shape) -> Unit,
) {
    Cell { cellModifier ->
        val shape = rememberCellShape()
        Box(
            modifier = cellModifier
                .then(modifier)
                .background(
                    color = backgroundColor,
                    shape = shape,
                ),
            contentAlignment = contentAlignment,
            propagateMinConstraints = propagateMinConstraints,
        ) {
            content(shape)
        }
    }
}