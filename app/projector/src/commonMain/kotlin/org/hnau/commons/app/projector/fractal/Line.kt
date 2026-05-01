package org.hnau.commons.app.projector.fractal

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
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.unit.constrainWidth
import androidx.compose.ui.unit.offset
import org.hnau.commons.app.projector.utils.Orientation
import org.hnau.commons.app.projector.utils.fold
import org.hnau.commons.kotlin.foldBoolean

enum class ForceFill {
    First, Last;

    companion object {

        val default: ForceFill
            get() = Last
    }
}

@Composable
fun Line(
    orientation: Orientation,
    modifier: Modifier = Modifier,
    arrangement: Arrangement.Horizontal = Arrangement.Start,
    reverseOrdering: Boolean = false,
    forceFill: ForceFill = ForceFill.default,
    content: @Composable () -> Unit,
) {
    val measurePolicy = remember(
        orientation,
        arrangement,
        reverseOrdering,
        forceFill,
    ) {
        object : MeasurePolicy {
            override fun MeasureScope.measure(
                measurables: List<Measurable>,
                constraints: Constraints,
            ): MeasureResult {

                val spacePx = arrangement.spacing.roundToPx()

                val spacesCount = (measurables.size - 1).coerceAtLeast(minimumValue = 0)
                val totalSpace = spacePx * spacesCount

                // 1. Вычисляем размер побочной оси (растягивание)


                val fixedCrossAxisConstraints = orientation.fold(
                    ifHorizontal = {

                        val fixedHeight = constraints.hasFixedHeight.foldBoolean(
                            ifTrue = { constraints.maxHeight },
                            ifFalse = {
                                val maxIntrinsic = measurables
                                    .maxOfOrNull { measurable ->
                                        measurable.maxIntrinsicHeight(
                                            width = constraints.hasBoundedWidth.foldBoolean(
                                                ifTrue = {
                                                    constraints.maxWidth
                                                },
                                                ifFalse = {
                                                    Int.MAX_VALUE
                                                },
                                            ),
                                        )
                                    }
                                    ?: 0

                                constraints.constrainHeight(maxIntrinsic)
                            },
                        )

                        constraints.copy(
                            minHeight = fixedHeight,
                            maxHeight = fixedHeight,
                        )
                    },
                    ifVertical = {

                        val fixedWidth = constraints.hasFixedWidth.foldBoolean(
                            ifTrue = { constraints.maxWidth },
                            ifFalse = {
                                val maxIntrinsic = measurables
                                    .maxOfOrNull { measurable ->
                                        measurable.maxIntrinsicWidth(
                                            height = constraints.hasBoundedHeight.foldBoolean(
                                                ifTrue = { constraints.maxHeight },
                                                ifFalse = { Int.MAX_VALUE },
                                            ),
                                        )
                                    }
                                    ?: 0

                                constraints.constrainWidth(maxIntrinsic)
                            },
                        )

                        constraints.copy(
                            minWidth = fixedWidth,
                            maxWidth = fixedWidth,
                        )
                    },
                )

                // 2. Формируем ограничения для детей (фиксация побочной оси)
                val childrenConstraints = orientation.fold(
                    ifHorizontal = {
                        fixedCrossAxisConstraints.copy(
                            minWidth = 0,
                        )
                    },
                    ifVertical = {
                        fixedCrossAxisConstraints.copy(
                            minHeight = 0,
                        )
                    },
                )

                val reverseForForceFill: Boolean = when (forceFill) {
                    ForceFill.First -> true
                    ForceFill.Last -> false
                }

                var usedByChildren = 0

                // 3. Измерение (уменьшаем доступное пространство после каждого элемента)
                val placeables = measurables
                    .asReversedIf(reverseForForceFill)
                    .mapIndexed { index, measurable ->

                        val isLast = index == measurables.lastIndex

                        val childConstraints = isLast.foldBoolean(
                            ifTrue = { fixedCrossAxisConstraints },
                            ifFalse = { childrenConstraints }
                        ).let { constraints ->
                            orientation.fold(
                                ifHorizontal = {
                                    constraints.offset(
                                        horizontal = -usedByChildren
                                    )
                                },
                                ifVertical = {
                                    constraints.offset(
                                        vertical = -usedByChildren
                                    )
                                }
                            )
                        }

                        val placeable = measurable.measure(
                            constraints = childConstraints,
                        )

                        val additionalSpace = isLast.foldBoolean(
                            ifTrue = { 0 },
                            ifFalse = { spacePx }
                        )

                        usedByChildren += orientation.fold(
                            ifHorizontal = { placeable.width },
                            ifVertical = { placeable.height },
                        ) + additionalSpace

                        placeable
                    }
                    .asReversedIf(reverseForForceFill)

                // 4. Логический порядок
                val orderedPlaceables = placeables.asReversedIf(reverseOrdering)

                // 5. Вычисление размера главной оси
                val mainAxisSize = orientation.fold(
                    ifHorizontal = {
                        val sumWidth = orderedPlaceables.sumOf(Placeable::width)
                        constraints.constrainWidth(sumWidth + totalSpace)
                    },
                    ifVertical = {
                        val sumHeight = orderedPlaceables.sumOf(Placeable::height)
                        constraints.constrainHeight(sumHeight + totalSpace)
                    },
                )

                // 6. Итоговые размеры контейнера
                val layoutWidth = orientation.fold(
                    ifHorizontal = { mainAxisSize },
                    ifVertical = { fixedCrossAxisConstraints.maxWidth },
                )

                val layoutHeight = orientation.fold(
                    ifHorizontal = { fixedCrossAxisConstraints.maxHeight },
                    ifVertical = { mainAxisSize },
                )

                // 7. Расчет позиций через Arrangement (до PlacementScope)
                val sizes = IntArray(
                    size = orderedPlaceables.size,
                ) { index ->
                    orientation.fold(
                        ifHorizontal = {
                            orderedPlaceables[index].width
                        },
                        ifVertical = {
                            orderedPlaceables[index].height
                        },
                    )
                }

                val positions = IntArray(
                    size = orderedPlaceables.size,
                )

                orientation.fold(
                    ifHorizontal = {
                        with(arrangement) {
                            arrange(
                                totalSize = layoutWidth,
                                sizes = sizes,
                                layoutDirection = layoutDirection,
                                outPositions = positions,
                            )
                        }
                    },
                    ifVertical = {
                        with(arrangement) {
                            arrange(
                                totalSize = layoutHeight,
                                sizes = sizes,
                                layoutDirection = LayoutDirection.Ltr,
                                outPositions = positions,
                            )
                        }
                    },
                )

                // 8. Размещение
                return layout(
                    width = layoutWidth,
                    height = layoutHeight,
                ) {
                    orientation.fold(
                        ifHorizontal = {
                            orderedPlaceables.forEachIndexed { index, placeable ->
                                placeable.place(
                                    x = positions[index],
                                    y = 0,
                                )
                            }
                        },
                        ifVertical = {
                            orderedPlaceables.forEachIndexed { index, placeable ->
                                placeable.place(
                                    x = 0,
                                    y = positions[index],
                                )
                            }
                        },
                    )
                }
            }

            // --- Интринсики ---

            override fun IntrinsicMeasureScope.minIntrinsicWidth(
                measurables: List<IntrinsicMeasurable>,
                height: Int,
            ): Int = orientation.fold(
                ifHorizontal = {
                    val spacing = arrangement.spacing.roundToPx()
                    val spacesCount = (measurables.size - 1).coerceAtLeast(
                        minimumValue = 0,
                    )
                    val sum = measurables.sumOf { measurable ->
                        measurable.minIntrinsicWidth(
                            height = height,
                        )
                    }

                    sum + spacing * spacesCount
                },
                ifVertical = {
                    measurables.maxOfOrNull { measurable ->
                        measurable.minIntrinsicWidth(
                            height = height,
                        )
                    } ?: 0
                },
            )

            override fun IntrinsicMeasureScope.maxIntrinsicWidth(
                measurables: List<IntrinsicMeasurable>,
                height: Int,
            ): Int = orientation.fold(
                ifHorizontal = {
                    val spacing = arrangement.spacing.roundToPx()
                    val spacesCount = (measurables.size - 1).coerceAtLeast(
                        minimumValue = 0,
                    )
                    val sum = measurables.sumOf { measurable ->
                        measurable.maxIntrinsicWidth(
                            height = height,
                        )
                    }

                    sum + spacing * spacesCount
                },
                ifVertical = {
                    measurables.maxOfOrNull { measurable ->
                        measurable.maxIntrinsicWidth(
                            height = height,
                        )
                    } ?: 0
                },
            )

            override fun IntrinsicMeasureScope.minIntrinsicHeight(
                measurables: List<IntrinsicMeasurable>,
                width: Int,
            ): Int = orientation.fold(
                ifHorizontal = {
                    measurables.maxOfOrNull { measurable ->
                        measurable.minIntrinsicHeight(
                            width = width,
                        )
                    } ?: 0
                },
                ifVertical = {
                    val spacing = arrangement.spacing.roundToPx()
                    val spacesCount = (measurables.size - 1).coerceAtLeast(
                        minimumValue = 0,
                    )
                    val sum = measurables.sumOf { measurable ->
                        measurable.minIntrinsicHeight(
                            width = width,
                        )
                    }

                    sum + spacing * spacesCount
                },
            )

            override fun IntrinsicMeasureScope.maxIntrinsicHeight(
                measurables: List<IntrinsicMeasurable>,
                width: Int,
            ): Int = orientation.fold(
                ifHorizontal = {
                    measurables.maxOfOrNull { measurable ->
                        measurable.maxIntrinsicHeight(
                            width = width,
                        )
                    } ?: 0
                },
                ifVertical = {
                    val spacing = arrangement.spacing.roundToPx()
                    val spacesCount = (measurables.size - 1).coerceAtLeast(
                        minimumValue = 0,
                    )
                    val sum = measurables.sumOf { measurable ->
                        measurable.maxIntrinsicHeight(
                            width = width,
                        )
                    }

                    sum + spacing * spacesCount
                },
            )
        }
    }

    Layout(
        content = content,
        modifier = modifier,
        measurePolicy = measurePolicy,
    )
}

private fun <T> List<T>.asReversedIf(
    reverse: Boolean,
): List<T> = reverse.foldBoolean(
    ifTrue = { asReversed() },
    ifFalse = { this },
)