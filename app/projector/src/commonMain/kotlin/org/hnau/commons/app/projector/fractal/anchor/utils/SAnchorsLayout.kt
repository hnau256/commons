package org.hnau.commons.app.projector.fractal.anchor.utils

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable
import arrow.core.NonEmptyList
import org.hnau.commons.app.projector.fractal.anchor.Anchor
import org.hnau.commons.app.projector.uikit.line.ext.IntSize
import org.hnau.commons.app.projector.uikit.line.ext.Offset
import org.hnau.commons.app.projector.uikit.line.ext.Size
import org.hnau.commons.app.projector.uikit.line.ext.across
import org.hnau.commons.app.projector.uikit.line.ext.along
import org.hnau.commons.app.projector.uikit.line.ext.constrainAcross
import org.hnau.commons.app.projector.uikit.line.ext.constrainAlong
import org.hnau.commons.app.projector.uikit.line.ext.copy
import org.hnau.commons.app.projector.uikit.line.ext.offset
import org.hnau.commons.app.projector.uikit.line.ext.placeRelative
import org.hnau.commons.app.projector.utils.Orientation
import org.hnau.commons.kotlin.foldBoolean

@Composable
context(_: Orientation)
internal fun SAnchorsLayout(
    anchors: NonEmptyList<Anchor>,
    rects: MutableList<Rect>,
    item: @Composable (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Layout(
        modifier = modifier,
        content = {
            repeat(anchors.size) { i ->
                Box(
                    propagateMinConstraints = true,
                ) {
                    item(i)
                }
            }
        },
    ) { measurables, constraints ->

        val (placeables, _, totalWeight) = measurables
            .foldIndexed(
                initial = Triple(
                    emptyList<Placeable>(),
                    0,
                    0f
                ),
            ) { i, (placeables, usedAlong, totalWeight), measurable ->
                val childConstraints = constraints.offset(
                    along = -usedAlong,
                ).copy(
                    minAlong = 0,
                )
                val placeable = measurable.measure(
                    constraints = childConstraints,
                )
                Triple(
                    placeables + placeable,
                    usedAlong + placeable.along,
                    totalWeight + anchors[i].weightBefore,
                )
            }

        val childrenAlong = placeables.sumOf { placeable -> placeable.along }

        val along = constraints.constrainAlong(
            along = childrenAlong
        )

        val across = constraints.constrainAcross(
            across = placeables.maxOf { placeable -> placeable.across },
        )

        val additionalAlongByWeight = (along - childrenAlong) / totalWeight

        val size = IntSize(
            along = along,
            across = across,
        )

        var alongOffset = 0f
        layout(
            width = size.width,
            height = size.height,
        ) {
            placeables.forEachIndexed { i, placeable ->
                val anchor = anchors[i].weightBefore
                val isLast = i == anchors.lastIndex
                val alongPosition = isLast.foldBoolean(
                    ifTrue = { along - placeable.along },
                    ifFalse = {
                        val result = alongOffset + anchor * additionalAlongByWeight
                        alongOffset = result + placeable.along
                        result.toInt()
                    }
                )
                placeable.placeRelative(
                    along = alongPosition,
                    across = (across - placeable.across) / 2,
                )
                rects[i] = Rect(
                    offset = Offset(
                        along = alongPosition.toFloat(),
                        across = 0f,
                    ),
                    size = Size(
                        along = placeable.along.toFloat(),
                        across = size.across.toFloat(),
                    )
                )
            }
        }
    }
}