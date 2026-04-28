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
import org.hnau.commons.kotlin.it

@Composable
fun TableScope.Subtable(
    configModifier: (Modifier) -> Modifier = ::it,
    content: @Composable TableScope.() -> Unit,
) {
    Cell { modifier ->
        Table(
            modifier = configModifier(modifier),
            orientation = orientation.opposite,
            corners = this,
            content = content,
        )
    }
}

@Composable
fun TableScope.CellBox(
    configModifier: (Modifier) -> Modifier = ::it,
    backgroundColor: Color = TableDefaults.cellColor,
    contentAlignment: Alignment = Alignment.Center,
    propagateMinConstraints: Boolean = false,
    content: @Composable BoxScope.(Shape) -> Unit,
) {
    Cell { modifier ->
        val shape = shape
        Box(
            modifier = modifier
                .let(configModifier)
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