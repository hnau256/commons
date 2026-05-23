package org.hnau.commons.app.projector.uikit.line

import androidx.compose.foundation.layout.Arrangement
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
import org.hnau.commons.app.projector.uikit.line.ext.arrangeForCorrectOrientation
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
    content: @Composable LineScope.() -> Unit,
) {
    val layoutDirection = LocalLayoutDirection.current
    Layout(
        modifier = modifier,
        measurePolicy = remember(
            orientation,
            separation,
            layoutDirection,
        ) {
            LineMeasurePolicy(
                orientation = orientation,
                separation = separation,
                layoutDirection = layoutDirection,
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
) : MeasurePolicy {

    override fun MeasureScope.measure(
        measurables: List<Measurable>,
        constraints: Constraints,
    ): MeasureResult = with(orientation) {

        measurables.forEachIndexed { i, measurable ->
            measurable
                .parentData
                ?.castOrNull<LineParentData>()
                ?.onPositionCallback
                ?.invoke(
                    LinePosition(
                        isFirst = i <= 0,
                        isLast = i >= measurables.lastIndex,
                    )
                )
        }

        val childrenAcross = calcIntrinsicAcross(
            measurables = measurables,
            max = true,
            along = constraints.maxAlong,
        )
        val across = constraints.constrainAcross(childrenAcross)

        val placeables = measure(
            measurables = measurables,
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
            along = placeables.sumOf { placeable -> placeable.along } + measurables.separationsSum(),
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
    private fun <I : IntrinsicMeasurable, O> IntrinsicMeasureScope.measure(
        measurables: List<I>,
        constraints: Constraints,
        measure: I.(Constraints) -> O,
        extractParentData: I.() -> Any?,
        extractAlong: O.() -> Int,
    ): List<O> {

        if (measurables.isEmpty()) {
            return emptyList()
        }

        fun I.extractLineParentData(): LineParentData =
            extractParentData().castOrElse<LineParentData> { emptyLineParentData }

        var forceWeight: Float? = null
        if (constraints.maxAlong != null) {
            val hasAtLeastOneWeight = measurables
                .any { measurable -> measurable.extractLineParentData().weight > 0f }
            if (!hasAtLeastOneWeight) {
                forceWeight = 1f
            }
        }

        var totalWeight = 0f
        val separationsSum = measurables.separationsSum()
        var usedAlong = 0

        val firstStepResult: List<Either<Float, O>> = measurables.map { measurable ->

            val weight = forceWeight ?: measurable.extractLineParentData().weight

            if (weight > 0f) {
                totalWeight += weight
                return@map weight.left()
            }

            val childConstraints = constraints
                .offset(along = -(usedAlong + separationsSum))
                .withoutMinAlong()

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
        across: Int
    ): Int = with(orientation) {
        measure(
            measurables = measurables,
            constraints = with(orientation) {
                Constraints(
                    maxAcross = across,
                )
            },
            measure = { constraints ->
                val across = constraints.maxAcross ?: Int.MAX_VALUE
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
        if (measurables.isEmpty()) {
            return@with 0
        }
        val alongWithoutSeparations = along.foldNullable(
            ifNull = { Int.MAX_VALUE },
            ifNotNull = { alongNotNull -> alongNotNull - measurables.separationsSum() },
        )
        measurables.maxOf { measurable ->
            max.foldBoolean(
                ifTrue = { measurable.maxIntrinsicAcross(alongWithoutSeparations) },
                ifFalse = { measurable.minIntrinsicAcross(alongWithoutSeparations) },
            )
        }
    }

    private fun IntrinsicMeasureScope.calcIntrinsic(
        direction: Direction,
        measurables: List<IntrinsicMeasurable>,
        max: Boolean,
        oppositeSize: Int
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
        oppositeSize = height,
    )

    override fun IntrinsicMeasureScope.minIntrinsicWidth(
        measurables: List<IntrinsicMeasurable>,
        height: Int
    ): Int = calcIntrinsic(
        direction = Orientation.Horizontal.compareWith(orientation),
        measurables = measurables,
        max = false,
        oppositeSize = height,
    )

    override fun IntrinsicMeasureScope.maxIntrinsicHeight(
        measurables: List<IntrinsicMeasurable>,
        width: Int
    ): Int = calcIntrinsic(
        direction = Orientation.Vertical.compareWith(orientation),
        measurables = measurables,
        max = true,
        oppositeSize = width,
    )

    override fun IntrinsicMeasureScope.minIntrinsicHeight(
        measurables: List<IntrinsicMeasurable>,
        width: Int
    ): Int = calcIntrinsic(
        direction = Orientation.Vertical.compareWith(orientation),
        measurables = measurables,
        max = false,
        oppositeSize = width,
    )
}

private val emptyLineParentData: LineParentData =
    LineParentData.createEmpty()