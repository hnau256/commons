package org.hnau.commons.app.projector.fractal

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.hnau.commons.app.projector.fractal.context.LocalFContext
import org.hnau.commons.app.projector.fractal.context.UpdateFContext
import org.hnau.commons.app.projector.fractal.size.SizeType
import org.hnau.commons.app.projector.fractal.size.units
import org.hnau.commons.app.projector.fractal.utils.Saturation
import org.hnau.commons.app.projector.fractal.utils.plus
import org.hnau.commons.app.projector.uikit.line.Line
import org.hnau.commons.app.projector.uikit.state.StateContent
import org.hnau.commons.app.projector.uikit.transition.TransitionSpec
import org.hnau.commons.app.projector.utils.Orientation
import org.hnau.commons.app.projector.utils.Side
import org.hnau.commons.app.projector.utils.fold
import org.hnau.commons.kotlin.foldNullable

@Composable
fun SItem(
    modifier: Modifier = Modifier,
    startAccessory: @Composable (() -> Unit)? = null,
    topAccessory: @Composable (() -> Unit)? = null,
    endAccessory: @Composable (() -> Unit)? = null,
    bottomAccessory: @Composable (() -> Unit)? = null,
    saturation: Saturation = Saturation.Active,
    content: @Composable () -> Unit,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Accessory(
            side = Side.Start,
            accessory = startAccessory,
            distanceOffset = 0,
            saturation = saturation,
        )
        Line(
            modifier = Modifier.weight(1f),
            orientation = Orientation.Vertical,
        ) {
            Accessory(
                side = Side.Top,
                accessory = topAccessory,
                distanceOffset = 1,
                saturation = Saturation.Neutral,
            )
            UpdateFContext(
                saturation = saturation,
            ) {
                content()
            }
            Accessory(
                side = Side.Bottom,
                accessory = bottomAccessory,
                distanceOffset = 1,
                saturation = Saturation.Neutral,
            )
        }
        Accessory(
            side = Side.End,
            accessory = endAccessory,
            distanceOffset = 0,
            saturation = saturation,
        )
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
        saturation = saturation,
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
            localAccessoryOrNull.foldNullable(
                ifNull = {},
                ifNotNull = { localAccessory ->
                    UpdateFContext(
                        update = {
                            copy(
                                distance = distance + distanceOffset,
                            )
                        }
                    ) {
                        val units = LocalFContext.current.distance.units
                        val paddingSize = SizeType.Small
                        val accessoryPadding = side.fold(
                            ifStart = {
                                PaddingValues(end = units.padding.along[paddingSize])
                            },
                            ifTop = {
                                PaddingValues(bottom = units.padding.across[paddingSize])
                            },
                            ifEnd = {
                                PaddingValues(start = units.padding.along[paddingSize])
                            },
                            ifBottom = {
                                PaddingValues(top = units.padding.across[paddingSize])
                            }
                        )
                        Box(modifier = Modifier.padding(accessoryPadding)) {
                            localAccessory()
                        }
                    }
                }
            )
        }
    }
}
