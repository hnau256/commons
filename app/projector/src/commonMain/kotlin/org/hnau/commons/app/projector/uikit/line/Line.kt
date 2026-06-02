package org.hnau.commons.app.projector.uikit.line

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.IntrinsicMeasurable
import androidx.compose.ui.layout.IntrinsicMeasureScope
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.constrain
import androidx.compose.ui.unit.dp
import arrow.core.Either
import arrow.core.getOrElse
import arrow.core.left
import arrow.core.right
import org.hnau.commons.app.projector.uikit.line.ext.Constraints
import org.hnau.commons.app.projector.uikit.line.ext.IntSize
import org.hnau.commons.app.projector.uikit.line.ext.along
import org.hnau.commons.app.projector.uikit.line.ext.constrainAcross
import org.hnau.commons.app.projector.uikit.line.ext.copy
import org.hnau.commons.app.projector.uikit.line.ext.maxAcross
import org.hnau.commons.app.projector.uikit.line.ext.maxAlong
import org.hnau.commons.app.projector.uikit.line.ext.maxIntrinsicAcross
import org.hnau.commons.app.projector.uikit.line.ext.maxIntrinsicAlong
import org.hnau.commons.app.projector.uikit.line.ext.minIntrinsicAcross
import org.hnau.commons.app.projector.uikit.line.ext.minIntrinsicAlong
import org.hnau.commons.app.projector.uikit.line.ext.offset
import org.hnau.commons.app.projector.uikit.line.ext.placeRelativeA
import org.hnau.commons.app.projector.uikit.line.ext.withoutMinAlong
import org.hnau.commons.app.projector.utils.Direction
import org.hnau.commons.app.projector.utils.Orientation
import org.hnau.commons.app.projector.utils.compareWith
import org.hnau.commons.app.projector.utils.fold
import org.hnau.commons.kotlin.castOrElse
import org.hnau.commons.kotlin.castOrNull
import org.hnau.commons.kotlin.foldBoolean
import org.hnau.commons.kotlin.foldNullable
import org.hnau.commons.kotlin.it

@Composable
fun Line(
    orientation: Orientation,
    modifier: Modifier = Modifier,
    separation: Dp = 0.dp,
    reverseOrdering: Boolean = false,
    content: @Composable LineScope.() -> Unit,
) {
    val layoutDirection = LocalLayoutDirection.current
    Layout(
        modifier = modifier,
        measurePolicy = remember(
            orientation,
            separation,
            layoutDirection,
            reverseOrdering,
        ) {
            LineMeasurePolicy(
                orientation = orientation,
                separation = separation,
                layoutDirection = layoutDirection,
                reverseOrdering = reverseOrdering,
            )
        },
        content = { lineScopeImpl.content() },
    )
}

private val lineScopeImpl: LineScope = object : LineScope {}

private data class LineMeasurePolicy(
    private val orientation: Orientation,
    private val separation: Dp,
    private val layoutDirection: LayoutDirection,
    private val reverseOrdering: Boolean,
) : MeasurePolicy {

    override fun MeasureScope.measure(
        measurables: List<Measurable>,
        constraints: Constraints,
    ): MeasureResult = with(orientation) {

        val orderedMeasurables = reverseOrdering.foldBoolean(
            ifTrue = { measurables.asReversed() },
            ifFalse = { measurables },
        )

        orderedMeasurables.forEachIndexed { i, measurable ->
            measurable
                .parentData
                ?.castOrNull<LineParentData>()
                ?.onPositionCallback
                ?.invoke(
                    LinePosition(
                        isFirst = i <= 0,
                        isLast = i >= orderedMeasurables.lastIndex,
                    )
                )
        }

        val childrenAcross = calcIntrinsicAcross(
            measurables = orderedMeasurables,
            max = true,
            along = constraints.maxAlong,
        )
        val across = constraints.constrainAcross(childrenAcross)

        val placeables = measure(
            useWeight = true,
            measurables = orderedMeasurables,
            constraints = constraints.copy(
                minAcross = across,
                maxAcross = across,
            ),
            extractParentData = Measurable::parentData,
            measure = Measurable::measure,
            extractAlong = { along },
        )

        val size = IntSize(
            across = across,
            along = placeables.sumOf { placeable -> placeable.along } + orderedMeasurables.separationsSum(),
        ).let(constraints::constrain)

        val separationPixels = separationPixels
        layout(
            width = size.width,
            height = size.height,
        ) {
            var along = 0
            placeables.forEach { placeable ->
                placeable.placeRelativeA(
                    along = along,
                    across = 0,
                )
                along += placeable.along + separationPixels
            }
        }
    }


    context(density: Density)
    private val separationPixels: Int
        get() = with(density) { separation.roundToPx() }

    context(density: Density)
    private fun Collection<IntrinsicMeasurable>.separationsSum(): Int =
        (size - 1) * separationPixels

    context(orientation: Orientation)
    private inline fun <I : IntrinsicMeasurable, O> IntrinsicMeasureScope.measure(
        useWeight: Boolean,
        measurables: List<I>,
        constraints: Constraints,
        measure: I.(Constraints) -> O,
        crossinline extractParentData: I.() -> Any?,
        extractAlong: O.() -> Int,
    ): List<O> {

        if (measurables.isEmpty()) {
            return emptyList()
        }

        val extractLineParentData: (
            measurable: I,
        ) -> LineParentData = { measurable ->
            measurable
                .extractParentData()
                .castOrElse<LineParentData> { emptyLineParentData }
        }

        val hasAtLeastOneWeight = useWeight && measurables
            .any { measurable -> measurable.let(extractLineParentData).weight > 0f }

        var totalWeight = 0f
        val separationsSum = measurables.separationsSum()
        var usedAlong = 0

        val firstStepResult: List<Either<Float, O>> = measurables.mapIndexed { i, measurable ->

            val weight = useWeight.foldBoolean(
                ifTrue = { measurable.let(extractLineParentData).weight },
                ifFalse = { 0f }
            )
            if (weight > 0f) {
                totalWeight += weight
                return@mapIndexed weight.left()
            }

            var childConstraints = constraints
                .offset(along = -(usedAlong + separationsSum))

            val isLast = i >= measurables.lastIndex
            if (hasAtLeastOneWeight || !isLast || !useWeight) {
                childConstraints = childConstraints.withoutMinAlong()
            }

            val measureResult = measurable.measure(childConstraints)
            val measuredResultAlong = measureResult.extractAlong()
            usedAlong += measuredResultAlong

            measureResult.right()
        }

        val allWeighedElementsAlong = lazy(LazyThreadSafetyMode.NONE) {
            constraints
                .offset(along = -usedAlong - separationsSum)
                .maxAlong
                ?: error("Unable use weight for infinity size")
        }

        return firstStepResult.mapIndexed { i, resultOrWeight ->
            resultOrWeight.getOrElse { weight ->
                val along = weight.takeIf { it > 0f }.foldNullable(
                    ifNull = { 0 },
                    ifNotNull = { (allWeighedElementsAlong.value * weight / totalWeight).toInt() }
                )
                val measurable = measurables[i]
                measurable.measure(
                    constraints.copy(
                        minAlong = along,
                        maxAlong = along,
                    )
                )
            }
        }
    }

    private fun IntrinsicMeasureScope.calcIntrinsicAlong(
        measurables: List<IntrinsicMeasurable>,
        max: Boolean,
        across: Int?
    ): Int = with(orientation) {
        measure(
            useWeight = false,
            measurables = measurables,
            constraints = Constraints(
                maxAcross = across,
            ),
            measure = { constraints ->
                val across = constraints.maxAcross ?: Constraints.Infinity
                max.foldBoolean(
                    ifTrue = { maxIntrinsicAlong(across) },
                    ifFalse = { minIntrinsicAlong(across) },
                )
            },
            extractAlong = ::it,
            extractParentData = IntrinsicMeasurable::parentData,
        )
            .sum()
            .plus(measurables.separationsSum())
    }

    private fun IntrinsicMeasureScope.calcIntrinsicAcross(
        measurables: List<IntrinsicMeasurable>,
        max: Boolean,
        along: Int?,
    ): Int = with(orientation) {
        measure(
            useWeight = true,
            measurables = measurables,
            constraints = Constraints(
                maxAlong = along,
            ),
            measure = { constraints ->
                val along = constraints.maxAlong ?: Constraints.Infinity
                val across = max.foldBoolean(
                    ifTrue = { maxIntrinsicAcross(along) },
                    ifFalse = { minIntrinsicAcross(along) },
                )
                val measurable = this
                measurable to across
            },
            extractAlong = {
                val (measurable, across) = this
                measurable.maxIntrinsicAlong(across)
            },
            extractParentData = IntrinsicMeasurable::parentData,
        ).maxOfOrNull { (_, across) ->
            across
        } ?: 0
    }

    private fun IntrinsicMeasureScope.calcIntrinsic(
        direction: Direction,
        measurables: List<IntrinsicMeasurable>,
        max: Boolean,
        oppositeSize: Int?
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
        }
    )

    override fun IntrinsicMeasureScope.maxIntrinsicWidth(
        measurables: List<IntrinsicMeasurable>,
        height: Int
    ): Int = calcIntrinsic(
        direction = Orientation.Horizontal.compareWith(orientation),
        measurables = measurables,
        max = true,
        oppositeSize = height.nullIfInfinity,
    )

    override fun IntrinsicMeasureScope.minIntrinsicWidth(
        measurables: List<IntrinsicMeasurable>,
        height: Int
    ): Int = calcIntrinsic(
        direction = Orientation.Horizontal.compareWith(orientation),
        measurables = measurables,
        max = false,
        oppositeSize = height.nullIfInfinity,
    )

    override fun IntrinsicMeasureScope.maxIntrinsicHeight(
        measurables: List<IntrinsicMeasurable>,
        width: Int
    ): Int = calcIntrinsic(
        direction = Orientation.Vertical.compareWith(orientation),
        measurables = measurables,
        max = true,
        oppositeSize = width.nullIfInfinity,
    )

    override fun IntrinsicMeasureScope.minIntrinsicHeight(
        measurables: List<IntrinsicMeasurable>,
        width: Int
    ): Int = calcIntrinsic(
        direction = Orientation.Vertical.compareWith(orientation),
        measurables = measurables,
        max = false,
        oppositeSize = width.nullIfInfinity,
    )
}

private val Int.nullIfInfinity: Int?
    get() = takeIf { it < Constraints.Infinity }

private val emptyLineParentData: LineParentData =
    LineParentData.createEmpty()