package org.hnau.commons.app.projector.utils

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.offset
import androidx.compose.ui.util.fastForEach

@Composable
fun Overcompose(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValuesZero,
    top: @Composable (PaddingValues) -> Unit = { paddingValues ->
        Box(
            modifier = Modifier.padding(paddingValues),
        )
    },
    bottom: @Composable (PaddingValues) -> Unit = { paddingValues ->
        Box(
            modifier = Modifier.padding(paddingValues),
        )
    },
    content: @Composable (contentPadding: PaddingValues) -> Unit,
) {
    val layoutDirection = LocalLayoutDirection.current

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
            content = { top(contentPadding.copy(bottom = 0.dp)) },
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
            content = { bottom(contentPadding.copy(top = 0.dp)) },
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
            content(
                PaddingValues(
                    top = topHeight.toDp(),
                    bottom = bottomHeight.toDp(),
                    start = contentPadding.calculateStartPadding(layoutDirection),
                    end = contentPadding.calculateEndPadding(layoutDirection),
                )
            )
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