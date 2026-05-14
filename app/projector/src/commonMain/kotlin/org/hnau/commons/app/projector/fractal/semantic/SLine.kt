package org.hnau.commons.app.projector.fractal.semantic

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.hnau.commons.app.projector.fractal.FLine
import org.hnau.commons.app.projector.fractal.ForceFill
import org.hnau.commons.app.projector.fractal.semantic.utils.LocalSContentPadding
import org.hnau.commons.app.projector.fractal.size.SizeType
import org.hnau.commons.app.projector.utils.Orientation
import org.hnau.commons.app.projector.utils.copy
import org.hnau.commons.app.projector.utils.fold

@Composable
fun SLine(
    orientation: Orientation,
    modifier: Modifier = Modifier,
    separation: SizeType = SizeType.Medium,
    alignment: Alignment.Horizontal = Alignment.Start,
    reverseOrdering: Boolean = false,
    forceFill: ForceFill? = null,
    content: @Composable () -> Unit,
) {
    val contentPadding = LocalSContentPadding.current
    val (childrenContentPadding, padding) = orientation.fold(
        ifHorizontal = {
            val childrenContentPadding = contentPadding.copy(
                start = 0.dp,
                end = 0.dp,
            )
            val padding = contentPadding.copy(
                top = 0.dp,
                bottom = 0.dp,
            )
            childrenContentPadding to padding
        },
        ifVertical = {
            val childrenContentPadding = contentPadding.copy(
                top = 0.dp,
                bottom = 0.dp,
            )
            val padding = contentPadding.copy(
                start = 0.dp,
                end = 0.dp,
            )
            childrenContentPadding to padding
        },
    )
    CompositionLocalProvider(
        LocalSContentPadding provides childrenContentPadding
    ) {
        FLine(
            orientation = orientation,
            modifier = modifier.padding(padding),
            separation = separation,
            alignment = alignment,
            reverseOrdering = reverseOrdering,
            forceFill = forceFill,
            content = content,
        )
    }
}