package org.hnau.commons.app.projector.fractal

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalLayoutDirection
import org.hnau.commons.app.projector.fractal.context.LocalFContext
import org.hnau.commons.app.projector.fractal.context.UpdateFContext
import org.hnau.commons.app.projector.fractal.size.units
import org.hnau.commons.app.projector.fractal.utils.Saturation
import org.hnau.commons.app.projector.fractal.utils.plus
import org.hnau.commons.app.projector.uikit.state.StateContent
import org.hnau.commons.app.projector.uikit.transition.TransitionSpec
import org.hnau.commons.app.projector.utils.Orientation
import org.hnau.commons.app.projector.utils.Side
import org.hnau.commons.app.projector.utils.fold
import org.hnau.commons.app.projector.utils.option
import org.hnau.commons.kotlin.foldNullable

@Composable
fun FItem(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = LocalFContext.current.distance.units.paddingValues.horizontal.small,
    shapeToClip: Shape? = LocalFContext.current.distance.units.shape,
    startAccessory: @Composable (() -> Unit)? = null,
    topAccessory: @Composable (() -> Unit)? = null,
    endAccessory: @Composable (() -> Unit)? = null,
    bottomAccessory: @Composable (() -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    UpdateFContext(
        update = {
            copy(
                saturation = Saturation.Active,
            )
        }
    ) {
        Row(
            modifier = modifier
                .then(
                    onClick.foldNullable(
                        ifNull = { Modifier },
                        ifNotNull = { onClickNotNull ->
                            Modifier
                                .option(
                                    shapeToClip?.let { shape ->
                                        Modifier.clip(shape)
                                    }
                                )
                                .clickable(onClick = onClickNotNull)
                        }
                    )
                ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Accessory(
                side = Side.Start,
                contentPadding = contentPadding,
                accessory = startAccessory,
                distanceOffset = 0,
                saturation = Saturation.Active,
            )
            Line(
                modifier = Modifier.weight(1f),
                orientation = Orientation.Vertical,
            ) {
                Accessory(
                    side = Side.Top,
                    contentPadding = contentPadding,
                    accessory = topAccessory,
                    distanceOffset = 1,
                    saturation = Saturation.Neutral,
                )
                content()
                Accessory(
                    side = Side.Bottom,
                    contentPadding = contentPadding,
                    accessory = bottomAccessory,
                    distanceOffset = 1,
                    saturation = Saturation.Neutral,
                )
            }
            Accessory(
                side = Side.End,
                contentPadding = contentPadding,
                accessory = endAccessory,
                distanceOffset = 0,
                saturation = Saturation.Active,
            )
        }
    }
}

@Composable
private fun Accessory(
    side: Side,
    contentPadding: PaddingValues,
    distanceOffset: Int,
    saturation: Saturation,
    accessory: @Composable (() -> Unit)?
) {
    UpdateFContext(
        update = {
            copy(
                saturation = saturation,
            )
        }
    ) {
        val align = side.fold(
            ifStart = { Alignment.CenterEnd },
            ifTop = { Alignment.BottomStart },
            ifEnd = { Alignment.CenterStart },
            ifBottom = { Alignment.TopStart },
        )
        accessory.StateContent(
            contentAlignment = align,
            transitionSpec = TransitionSpec.remember(align, align),
            label = "Accessory${side}OrPlaceholder",
            contentKey = { it != null },
        ) { localAccessoryOrNull ->
            val layoutDirection = LocalLayoutDirection.current
            val startPadding = contentPadding.calculateStartPadding(layoutDirection)
            val topPadding = contentPadding.calculateTopPadding()
            val endPadding = contentPadding.calculateEndPadding(layoutDirection)
            val bottomPadding = contentPadding.calculateBottomPadding()
            localAccessoryOrNull.foldNullable(
                ifNull = {
                    Spacer(
                        modifier = side.fold(
                            ifStart = {
                                Modifier.size(
                                    width = startPadding,
                                    height = topPadding + bottomPadding,
                                )
                            },
                            ifTop = { Modifier.height(topPadding) },
                            ifEnd = {
                                Modifier.size(
                                    width = endPadding,
                                    height = topPadding + bottomPadding,
                                )
                            },
                            ifBottom = { Modifier.height(bottomPadding) },
                        ),
                    )
                },
                ifNotNull = { localAccessory ->
                    UpdateFContext(
                        update = {
                            copy(
                                distance = distance + distanceOffset,
                            )
                        }
                    ) {
                        val units = LocalFContext.current.distance.units
                        val alongSpace = units.padding.along.small
                        val acrossSpace = units.padding.across.small
                        Box(
                            modifier = side.fold(
                                ifStart = {
                                    Modifier.padding(
                                        end = alongSpace,
                                        start = startPadding,
                                        top = topPadding,
                                        bottom = bottomPadding,
                                    )
                                },
                                ifTop = {
                                    Modifier.padding(
                                        bottom = acrossSpace,
                                        top = topPadding,
                                    )
                                },
                                ifEnd = {
                                    Modifier.padding(
                                        start = alongSpace,
                                        end = endPadding,
                                        top = topPadding,
                                        bottom = bottomPadding,
                                    )
                                },
                                ifBottom = {
                                    Modifier.padding(
                                        top = acrossSpace,
                                        bottom = bottomPadding,
                                    )
                                }
                            ),
                        ) {
                            localAccessory()
                        }
                    }
                }
            )
        }
    }
}