package org.hnau.commons.app.projector.fractal.semantic

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import org.hnau.commons.app.projector.fractal.ForceFill
import org.hnau.commons.app.projector.fractal.Line
import org.hnau.commons.app.projector.fractal.context.LocalFContext
import org.hnau.commons.app.projector.fractal.semantic.utils.LocalSContentPadding
import org.hnau.commons.app.projector.fractal.size.SizeType
import org.hnau.commons.app.projector.fractal.size.units
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
            val layoutDirection = LocalLayoutDirection.current
            val start = contentPadding.calculateStartPadding(layoutDirection)
            val end = contentPadding.calculateEndPadding(layoutDirection)
            val separationDiv2 =
                LocalFContext.current.distance.units.padding.horizontal[separation] / 2
            val childrenContentPadding = contentPadding.copy(
                start = separationDiv2,
                end = separationDiv2,
            )
            val padding = contentPadding.copy(
                top = 0.dp,
                bottom = 0.dp,
                start = start - separationDiv2,
                end = end - separationDiv2,
            )
            childrenContentPadding to padding
        },
        ifVertical = {
            val top = contentPadding.calculateTopPadding()
            val bottom = contentPadding.calculateBottomPadding()
            val separationDiv2 =
                LocalFContext.current.distance.units.padding.vertical[separation] / 2
            val childrenContentPadding = contentPadding.copy(
                top = separationDiv2,
                bottom = separationDiv2,
            )
            val padding = contentPadding.copy(
                top = top - separationDiv2,
                bottom = bottom - separationDiv2,
                start = 0.dp,
                end = 0.dp,
            )
            childrenContentPadding to padding
        },
    )
    CompositionLocalProvider(
        LocalSContentPadding provides childrenContentPadding
    ) {
        Line(
            orientation = orientation,
            modifier = modifier.padding(padding),
            arrangement = Arrangement.aligned(alignment),
            reverseOrdering = reverseOrdering,
            forceFill = forceFill,
            content = content,
        )
    }
}