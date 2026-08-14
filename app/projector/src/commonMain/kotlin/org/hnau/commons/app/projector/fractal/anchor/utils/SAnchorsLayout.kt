package org.hnau.commons.app.projector.fractal.anchor.utils

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable
import arrow.core.NonEmptyList
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
import kotlin.math.roundToInt

@Composable
context(_: Orientation)
internal fun SAnchorsLayout(
    weights: NonEmptyList<Float>,
    rects: MutableList<Rect>,
    item: @Composable (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val (normalizedWeights, totalWeight) = remember(weights) {

        val nonNegativeWeights = weights
            .map { weight -> weight.coerceAtLeast(0f) }

        val atLeastOnePositiveWeight = nonNegativeWeights.any { it > 0 }
        val weights = atLeastOnePositiveWeight.foldBoolean(
            ifTrue = { nonNegativeWeights },
            ifFalse = { nonNegativeWeights.map { 1f } },
        )

        val totalWeight = weights.sum()

        weights to totalWeight
    }

    Layout(
        modifier = modifier,
        content = {
            repeat(normalizedWeights.size + 1) { i ->
                Box(
                    propagateMinConstraints = true,
                ) {
                    item(i)
                }
            }
        },
    ) { measurables, constraints ->

        val (placeables, _) = measurables
            .fold(
                initial = Pair(
                    emptyList<Placeable>(),
                    0,
                ),
            ) { (placeables, usedAlong), measurable ->
                val childConstraints = constraints.offset(
                    along = -usedAlong,
                ).copy(
                    minAlong = 0,
                )
                val placeable = measurable.measure(
                    constraints = childConstraints,
                )
                Pair(
                    placeables + placeable,
                    usedAlong + placeable.along,
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
            rects.clear()
            placeables.forEachIndexed { i, placeable ->

                val weight = i
                    .minus(1)
                    .takeIf { it >= 0 }
                    ?.let(normalizedWeights::get)
                    ?: 0f

                val alongStart = alongOffset + weight * additionalAlongByWeight
                alongOffset = alongStart + placeable.along
                placeable.placeRelative(
                    along = alongStart.roundToInt(),
                    across = (across - placeable.across) / 2,
                )
                rects.add(
                    Rect(
                        offset = Offset(
                            along = alongStart,
                            across = 0f,
                        ),
                        size = Size(
                            along = placeable.along.toFloat(),
                            across = size.across.toFloat(),
                        )
                    )
                )
            }
        }
    }
}