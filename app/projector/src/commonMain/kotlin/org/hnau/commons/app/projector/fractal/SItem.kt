package org.hnau.commons.app.projector.fractal

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import org.hnau.commons.app.projector.fractal.context.LocalFContext
import org.hnau.commons.app.projector.fractal.context.UpdateFContext
import org.hnau.commons.app.projector.fractal.utils.LocalSContentPadding
import org.hnau.commons.app.projector.fractal.size.units
import org.hnau.commons.app.projector.fractal.utils.Saturation
import org.hnau.commons.app.projector.fractal.utils.plus
import org.hnau.commons.app.projector.uikit.state.StateContent
import org.hnau.commons.app.projector.uikit.transition.TransitionSpec
import org.hnau.commons.app.projector.utils.Orientation
import org.hnau.commons.app.projector.utils.Side
import org.hnau.commons.app.projector.utils.copy
import org.hnau.commons.app.projector.utils.fold
import org.hnau.commons.kotlin.foldNullable

@Composable
fun SItem(
    modifier: Modifier = Modifier,
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
                            Modifier.clickable(onClick = onClickNotNull)
                        }
                    )
                ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CompositionLocalProvider(
                LocalSContentPadding provides LocalSContentPadding.current.copy(end = 0.dp)
            ) {
                Accessory(
                    side = Side.Start,
                    accessory = startAccessory,
                    distanceOffset = 0,
                    saturation = Saturation.Active,
                )
            }
            CompositionLocalProvider(
                LocalSContentPadding provides LocalSContentPadding.current.copy(start = 0.dp, end = 0.dp)
            ) {
                SLine(
                    modifier = Modifier.weight(1f),
                    orientation = Orientation.Vertical,
                    separation = null,
                ) {
                    Accessory(
                        side = Side.Top,
                        accessory = topAccessory,
                        distanceOffset = 1,
                        saturation = Saturation.Neutral,
                    )
                    content()
                    Accessory(
                        side = Side.Bottom,
                        accessory = bottomAccessory,
                        distanceOffset = 1,
                        saturation = Saturation.Neutral,
                    )
                }
            }
            CompositionLocalProvider(
                LocalSContentPadding provides LocalSContentPadding.current.copy(start = 0.dp)
            ) {
                Accessory(
                    side = Side.End,
                    accessory = endAccessory,
                    distanceOffset = 0,
                    saturation = Saturation.Active,
                )
            }
        }
    }
}

@Composable
private fun Accessory(
    side: Side,
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
        val contentPadding = LocalSContentPadding.current
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
                        val alongSpace = units.padding.along.extraSmall
                        val acrossSpace = units.padding.across.extraSmall
                        val accessoryContentPadding = side.fold(
                            ifStart = {
                                PaddingValues(
                                    end = alongSpace,
                                    start = startPadding,
                                    top = topPadding,
                                    bottom = bottomPadding,
                                )
                            },
                            ifTop = {
                                PaddingValues(
                                    bottom = acrossSpace,
                                    top = topPadding,
                                )
                            },
                            ifEnd = {
                                PaddingValues(
                                    start = alongSpace,
                                    end = endPadding,
                                    top = topPadding,
                                    bottom = bottomPadding,
                                )
                            },
                            ifBottom = {
                                PaddingValues(
                                    top = acrossSpace,
                                    bottom = bottomPadding,
                                )
                            }
                        )
                        CompositionLocalProvider(
                            value = LocalSContentPadding provides accessoryContentPadding,
                            content = localAccessory,
                        )
                    }
                }
            )
        }
    }
}