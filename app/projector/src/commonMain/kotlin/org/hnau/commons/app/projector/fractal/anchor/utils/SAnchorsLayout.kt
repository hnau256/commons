package org.hnau.commons.app.projector.fractal.anchor.utils

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.IntrinsicMeasurable
import androidx.compose.ui.layout.IntrinsicMeasureScope
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.unit.Constraints
import arrow.core.NonEmptyList
import org.hnau.commons.app.projector.uikit.line.ext.IntSize
import org.hnau.commons.app.projector.uikit.line.ext.Offset
import org.hnau.commons.app.projector.uikit.line.ext.Size
import org.hnau.commons.app.projector.uikit.line.ext.across
import org.hnau.commons.app.projector.uikit.line.ext.along
import org.hnau.commons.app.projector.uikit.line.ext.constrainAcross
import org.hnau.commons.app.projector.uikit.line.ext.constrainAlong
import org.hnau.commons.app.projector.uikit.line.ext.copy
import org.hnau.commons.app.projector.uikit.line.ext.maxIntrinsicAcross
import org.hnau.commons.app.projector.uikit.line.ext.maxIntrinsicAlong
import org.hnau.commons.app.projector.uikit.line.ext.minIntrinsicAcross
import org.hnau.commons.app.projector.uikit.line.ext.minIntrinsicAlong
import org.hnau.commons.app.projector.uikit.line.ext.offset
import org.hnau.commons.app.projector.uikit.line.ext.placeRelative
import org.hnau.commons.app.projector.utils.Direction
import org.hnau.commons.app.projector.utils.Orientation
import org.hnau.commons.app.projector.utils.compareWith
import org.hnau.commons.app.projector.utils.fold
import org.hnau.commons.kotlin.foldBoolean
import kotlin.math.roundToInt

@Composable
context(orientation: Orientation)
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

    val measurePolicy = remember(
        orientation,
        normalizedWeights,
        totalWeight,
        rects,
    ) {
        SAnchorsMeasurePolicy(
            orientation = orientation,
            normalizedWeights = normalizedWeights,
            totalWeight = totalWeight,
            rects = rects,
        )
    }

    Layout(
        modifier = modifier,
        measurePolicy = measurePolicy,
        content = {
            repeat(normalizedWeights.size + 1) { i ->
                Box(
                    propagateMinConstraints = true,
                ) {
                    item(i)
                }
            }
        },
    )
}

private class SAnchorsMeasurePolicy(
    private val orientation: Orientation,
    private val normalizedWeights: List<Float>,
    private val totalWeight: Float,
    private val rects: MutableList<Rect>,
) : MeasurePolicy {

    override fun MeasureScope.measure(
        measurables: List<Measurable>,
        constraints: Constraints,
    ): MeasureResult = with(orientation) {

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

    private fun IntrinsicMeasureScope.calcIntrinsicAlong(
        measurables: List<IntrinsicMeasurable>,
        max: Boolean,
        across: Int?,
    ): Int = with(orientation) {
        val acrossValue = across ?: Constraints.Infinity
        measurables.sumOf { measurable ->
            max.foldBoolean(
                ifTrue = { measurable.maxIntrinsicAlong(acrossValue) },
                ifFalse = { measurable.minIntrinsicAlong(acrossValue) },
            )
        }
    }

    // The default intrinsic measurement reuses measure() with children whose along
    // is reported as the whole remaining space, making all following children get
    // along = 0. Consume intrinsic along instead to keep the remaining along valid.
    private fun IntrinsicMeasureScope.calcIntrinsicAcross(
        measurables: List<IntrinsicMeasurable>,
        max: Boolean,
        along: Int?,
    ): Int = with(orientation) {
        val availableAlong = along ?: Constraints.Infinity
        var usedAlong = 0
        var maxAcross = 0

        measurables.forEach { measurable ->
            val childMaxAlong = (availableAlong - usedAlong).coerceAtLeast(0)
            val childAcross = max.foldBoolean(
                ifTrue = { measurable.maxIntrinsicAcross(childMaxAlong) },
                ifFalse = { measurable.minIntrinsicAcross(childMaxAlong) },
            )
            val childAlong = max.foldBoolean(
                ifTrue = { measurable.maxIntrinsicAlong(childAcross) },
                ifFalse = { measurable.minIntrinsicAlong(childAcross) },
            )
            usedAlong += childAlong.coerceAtMost(childMaxAlong)
            maxAcross = maxAcross.coerceAtLeast(childAcross)
        }

        maxAcross
    }

    private fun IntrinsicMeasureScope.calcIntrinsic(
        direction: Direction,
        measurables: List<IntrinsicMeasurable>,
        max: Boolean,
        oppositeSize: Int?,
    ): Int = direction.fold(
        ifAlong = {
            calcIntrinsicAlong(
                measurables = measurables,
                max = max,
                across = oppositeSize,
            )
        },
        ifAcross = {
            calcIntrinsicAcross(
                measurables = measurables,
                max = max,
                along = oppositeSize,
            )
        },
    )

    override fun IntrinsicMeasureScope.maxIntrinsicWidth(
        measurables: List<IntrinsicMeasurable>,
        height: Int,
    ): Int = calcIntrinsic(
        direction = Orientation.Horizontal.compareWith(orientation),
        measurables = measurables,
        max = true,
        oppositeSize = height.nullIfInfinity,
    )

    override fun IntrinsicMeasureScope.minIntrinsicWidth(
        measurables: List<IntrinsicMeasurable>,
        height: Int,
    ): Int = calcIntrinsic(
        direction = Orientation.Horizontal.compareWith(orientation),
        measurables = measurables,
        max = false,
        oppositeSize = height.nullIfInfinity,
    )

    override fun IntrinsicMeasureScope.maxIntrinsicHeight(
        measurables: List<IntrinsicMeasurable>,
        width: Int,
    ): Int = calcIntrinsic(
        direction = Orientation.Vertical.compareWith(orientation),
        measurables = measurables,
        max = true,
        oppositeSize = width.nullIfInfinity,
    )

    override fun IntrinsicMeasureScope.minIntrinsicHeight(
        measurables: List<IntrinsicMeasurable>,
        width: Int,
    ): Int = calcIntrinsic(
        direction = Orientation.Vertical.compareWith(orientation),
        measurables = measurables,
        max = false,
        oppositeSize = width.nullIfInfinity,
    )
}

private val Int.nullIfInfinity: Int?
    get() = takeIf { it < Constraints.Infinity }
