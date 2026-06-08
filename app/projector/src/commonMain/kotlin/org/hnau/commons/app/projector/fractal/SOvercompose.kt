package org.hnau.commons.app.projector.fractal

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.offset
import androidx.compose.ui.util.fastForEach
import org.hnau.commons.app.projector.fractal.padding.LocalContentPadding
import org.hnau.commons.app.projector.fractal.padding.LocalContentPaddingBox
import org.hnau.commons.app.projector.utils.copy

@Composable
fun SOvercompose(
    modifier: Modifier = Modifier,
    top: @Composable () -> Unit = { LocalContentPaddingBox {} },
    bottom: @Composable () -> Unit = { LocalContentPaddingBox {} },
    content: @Composable () -> Unit,
) {
    val layoutDirection = LocalLayoutDirection.current
    val contentPadding = LocalContentPadding.current

    SubcomposeLayout(
        modifier = modifier,
    ) { constraints ->

        val maxWidth = constraints.maxWidth
        val maxHeight = constraints.maxHeight

        val topConstraints = constraints.copy(
            minWidth = maxWidth,
            minHeight = 0,
        )

        val topPlaceables = subcompose(
            slotId = "top",
            content = {
                CompositionLocalProvider(
                    value = LocalContentPadding provides contentPadding.copy(bottom = 0.dp),
                ) {
                    top()
                }
            },
        ).map { topMeasurable ->
            topMeasurable.measure(topConstraints)
        }

        val topHeight = topPlaceables
            .maxOfOrNull(Placeable::measuredHeight)
            ?: 0

        val bottomConstraints = topConstraints.offset(
            vertical = -topHeight,
        )

        val bottomPlaceables = subcompose(
            slotId = "bottom",
            content = {
                CompositionLocalProvider(
                    value = LocalContentPadding provides contentPadding.copy(top = 0.dp),
                ) {
                    bottom()
                }
            },
        ).map { bottomMeasurable ->
            bottomMeasurable.measure(bottomConstraints)
        }

        val bottomHeight = bottomPlaceables
            .maxOfOrNull(Placeable::measuredHeight)
            ?: 0

        val contentConstraints = topConstraints.copy(
            minHeight = maxHeight,
        )
        val contentPlaceables = subcompose(
            slotId = "content",
        ) {
            CompositionLocalProvider(
                value = LocalContentPadding provides PaddingValues(
                    top = topHeight.toDp(),
                    bottom = bottomHeight.toDp(),
                    start = contentPadding.calculateStartPadding(layoutDirection),
                    end = contentPadding.calculateEndPadding(layoutDirection),
                ),
            ) {
                content()
            }
        }
            .map { contentMeasurable ->
                contentMeasurable.measure(contentConstraints)
            }

        layout(
            width = maxWidth,
            height = maxHeight,
        ) {

            contentPlaceables.fastForEach { contentPlaceable ->
                contentPlaceable.placeRelative(
                    x = 0,
                    y = 0,
                )
            }

            bottomPlaceables.fastForEach { bottomPlaceable ->
                bottomPlaceable.placeRelative(
                    x = 0,
                    y = maxHeight - bottomPlaceable.measuredHeight,
                )
            }

            topPlaceables.fastForEach { topPlaceable ->
                topPlaceable.placeRelative(
                    x = 0,
                    y = 0,
                )
            }
        }
    }
}