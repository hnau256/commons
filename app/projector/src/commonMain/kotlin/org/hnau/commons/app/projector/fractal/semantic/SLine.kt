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
import androidx.compose.ui.unit.min
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
    val fContext = LocalFContext.current
    val separationSize = fContext.distance.units.padding[orientation][separation]
    val contentPadding = LocalSContentPadding.current
    val (childrenContentPadding, padding, actualSeparation) = orientation.fold(
        ifHorizontal = {

            val layoutDirection = LocalLayoutDirection.current


            val start = contentPadding.calculateStartPadding(layoutDirection)
            val end = contentPadding.calculateEndPadding(layoutDirection)

            val itemExpansion = min(
                separationSize / 2,
                min(start, end),
            )

            val childrenContentPadding = contentPadding.copy(
                start = itemExpansion,
                end = itemExpansion,
            )
            val padding = contentPadding.copy(
                start = start - itemExpansion,
                end = end - itemExpansion,
                top = 0.dp,
                bottom = 0.dp,
            )

            Triple(
                childrenContentPadding,
                padding,
                separationSize - itemExpansion * 2,
            )
        },
        ifVertical = {

            val top = contentPadding.calculateTopPadding()
            val bottom = contentPadding.calculateTopPadding()

            val itemExpansion = min(
                separationSize / 2,
                min(top, bottom),
            )

            val childrenContentPadding = contentPadding.copy(
                top = itemExpansion,
                bottom = itemExpansion,
            )
            val padding = contentPadding.copy(
                top = top - itemExpansion,
                bottom = bottom - itemExpansion,
                start = 0.dp,
                end = 0.dp,
            )

            Triple(
                childrenContentPadding,
                padding,
                separationSize - itemExpansion * 2,
            )
        },
    )
    Line(
        orientation = orientation,
        modifier = modifier.padding(padding),
        arrangement = Arrangement.spacedBy(actualSeparation, alignment),
        reverseOrdering = reverseOrdering,
        forceFill = forceFill,
        content = {
            CompositionLocalProvider(
                LocalSContentPadding provides childrenContentPadding
            ) {
                content()
            }
        },
    )
}