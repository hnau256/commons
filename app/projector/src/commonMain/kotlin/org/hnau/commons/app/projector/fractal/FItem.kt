package org.hnau.commons.app.projector.fractal

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import org.hnau.commons.app.projector.fractal.context.LocalFContext
import org.hnau.commons.app.projector.fractal.context.UpdateFContext
import org.hnau.commons.app.projector.fractal.size.SizeType
import org.hnau.commons.app.projector.fractal.size.units
import org.hnau.commons.app.projector.fractal.utils.plus
import org.hnau.commons.app.projector.uikit.state.StateContent
import org.hnau.commons.app.projector.uikit.transition.TransitionSpec
import org.hnau.commons.app.projector.utils.Orientation
import org.hnau.commons.app.projector.utils.Side
import org.hnau.commons.app.projector.utils.fold
import org.hnau.commons.app.projector.utils.option
import org.hnau.commons.app.projector.utils.orientation
import org.hnau.commons.kotlin.foldNullable

@Composable
fun FItem(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = LocalFContext.current.distance.units.paddingValues[SizeType.Medium],
    shapeToClip: Shape? = LocalFContext.current.distance.units.shape,
    startAccessory: @Composable (() -> Unit)? = null,
    topAccessory: @Composable (() -> Unit)? = null,
    endAccessory: @Composable (() -> Unit)? = null,
    bottomAccessory: @Composable (() -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit,
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
        val layoutDirection = LocalLayoutDirection.current
        Accessory(
            side = Side.Start,
            placeholderSize = contentPadding.calculateStartPadding(layoutDirection),
            accessory = startAccessory,
        )
        Line(
            modifier = Modifier.weight(1f),
            orientation = Orientation.Vertical,
        ) {
            Accessory(
                side = Side.Top,
                placeholderSize = contentPadding.calculateTopPadding(),
                accessory = topAccessory,
            )
            content()
            Accessory(
                side = Side.Bottom,
                placeholderSize = contentPadding.calculateBottomPadding(),
                accessory = bottomAccessory,
            )
        }
        Accessory(
            side = Side.End,
            placeholderSize = contentPadding.calculateEndPadding(layoutDirection),
            accessory = endAccessory,
        )
    }
}

@Composable
private fun Accessory(
    side: Side,
    placeholderSize: Dp,
    accessory: @Composable (() -> Unit)?
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
            ifNull = {
                Spacer(
                    modifier = side.fold(
                        ifStart = { Modifier.width(placeholderSize) },
                        ifTop = { Modifier.height(placeholderSize) },
                        ifEnd = { Modifier.width(placeholderSize) },
                        ifBottom = { Modifier.height(placeholderSize) },
                    )
                )
            },
            ifNotNull = { localAccessory ->
                UpdateFContext(
                    update = {
                        copy(
                            distance = distance + 1,
                        )
                    }
                ) {
                    val space = LocalFContext.current.distance.units.padding[side.orientation].small
                    Box(
                        modifier = side.fold(
                            ifStart = { Modifier.padding(end = space, start = placeholderSize) },
                            ifTop = { Modifier.padding(bottom = space, top = placeholderSize) },
                            ifEnd = { Modifier.padding(start = space, end = placeholderSize) },
                            ifBottom = { Modifier.padding(top = space, bottom = placeholderSize) }
                        ),
                    ) {
                        localAccessory()
                    }
                }
            }
        )
    }
}