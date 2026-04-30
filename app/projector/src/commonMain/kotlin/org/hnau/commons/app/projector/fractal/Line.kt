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
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.LayoutDirection
import org.hnau.commons.app.projector.utils.Orientation
import org.hnau.commons.app.projector.utils.fold
import org.hnau.commons.kotlin.foldBoolean

@Composable
fun Line(
    orientation: Orientation,
    modifier: Modifier = Modifier,
    arrangement: Arrangement.Horizontal = Arrangement.Start,
    reverseOrdering: Boolean = false,
    content: @Composable () -> Unit,
) {
    val measurePolicy = remember(
        orientation,
        arrangement,
        reverseOrdering,
    ) {
        object : MeasurePolicy {
            override fun MeasureScope.measure(
                measurables: List<Measurable>,
                constraints: Constraints,
            ): MeasureResult {
                val spacePx = arrangement.spacing.roundToPx()
                val spacesCount = (measurables.size - 1).coerceAtLeast(
                    minimumValue = 0,
                )
                val totalSpace = spacePx * spacesCount

                // 1. Вычисляем размер побочной оси (растягивание)
                val crossAxisTargetSize = orientation.fold(
                    ifHorizontal = {
                        constraints.hasFixedHeight.foldBoolean(
                            ifTrue = {
                                constraints.maxHeight
                            },
                            ifFalse = {
                                val maxIntrinsic = measurables.maxOfOrNull { measurable ->
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
                                } ?: 0

                                maxIntrinsic.coerceIn(
                                    minimumValue = constraints.minHeight,
                                    maximumValue = constraints.maxHeight,
                                )
                            },
                        )
                    },
                    ifVertical = {
                        constraints.hasFixedWidth.foldBoolean(
                            ifTrue = {
                                constraints.maxWidth
                            },
                            ifFalse = {
                                val maxIntrinsic = measurables.maxOfOrNull { measurable ->
                                    measurable.maxIntrinsicWidth(
                                        height = constraints.hasBoundedHeight.foldBoolean(
                                            ifTrue = {
                                                constraints.maxHeight
                                            },
                                            ifFalse = {
                                                Int.MAX_VALUE
                                            },
                                        ),
                                    )
                                } ?: 0

                                maxIntrinsic.coerceIn(
                                    minimumValue = constraints.minWidth,
                                    maximumValue = constraints.maxWidth,
                                )
                            },
                        )
                    },
                )

                // 2. Формируем ограничения для детей (фиксация побочной оси)
                val childConstraints = orientation.fold(
                    ifHorizontal = {
                        constraints.copy(
                            minWidth = 0,
                            maxWidth = constraints.hasBoundedWidth.foldBoolean(
                                ifTrue = {
                                    constraints.maxWidth
                                },
                                ifFalse = {
                                    Int.MAX_VALUE
                                },
                            ),
                            minHeight = crossAxisTargetSize,
                            maxHeight = crossAxisTargetSize,
                        )
                    },
                    ifVertical = {
                        constraints.copy(
                            minWidth = crossAxisTargetSize,
                            maxWidth = crossAxisTargetSize,
                            minHeight = 0,
                            maxHeight = constraints.hasBoundedHeight.foldBoolean(
                                ifTrue = {
                                    constraints.maxHeight
                                },
                                ifFalse = {
                                    Int.MAX_VALUE
                                },
                            ),
                        )
                    },
                )

                // 3. Измерение
                val placeables = measurables.map { measurable ->
                    measurable.measure(
                        constraints = childConstraints,
                    )
                }

                // 4. Логический порядок
                val orderedPlaceables = reverseOrdering.foldBoolean(
                    ifTrue = {
                        placeables.asReversed()
                    },
                    ifFalse = {
                        placeables
                    },
                )

                // 5. Вычисление размера главной оси
                val mainAxisSize = orientation.fold(
                    ifHorizontal = {
                        val sumWidth = orderedPlaceables.sumOf { placeable ->
                            placeable.width
                        }

                        (sumWidth + totalSpace).coerceIn(
                            minimumValue = constraints.minWidth,
                            maximumValue = constraints.maxWidth,
                        )
                    },
                    ifVertical = {
                        val sumHeight = orderedPlaceables.sumOf { placeable ->
                            placeable.height
                        }

                        (sumHeight + totalSpace).coerceIn(
                            minimumValue = constraints.minHeight,
                            maximumValue = constraints.maxHeight,
                        )
                    },
                )

                // 6. Итоговые размеры контейнера
                val layoutWidth = orientation.fold(
                    ifHorizontal = {
                        mainAxisSize
                    },
                    ifVertical = {
                        crossAxisTargetSize
                    },
                )

                val layoutHeight = orientation.fold(
                    ifHorizontal = {
                        crossAxisTargetSize
                    },
                    ifVertical = {
                        mainAxisSize
                    },
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