package org.hnau.commons.app.projector.fractal

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import org.hnau.commons.app.projector.fractal.context.LocalFContext
import org.hnau.commons.app.projector.fractal.context.UpdateFContext
import org.hnau.commons.app.projector.fractal.size.SizeType
import org.hnau.commons.app.projector.fractal.size.fPadding
import org.hnau.commons.app.projector.fractal.size.units
import org.hnau.commons.app.projector.uikit.state.NullableStateContent
import org.hnau.commons.app.projector.uikit.transition.TransitionSpec
import org.hnau.commons.app.projector.utils.Orientation
import org.hnau.commons.app.projector.utils.Side
import org.hnau.commons.app.projector.utils.fold
import org.hnau.commons.app.projector.utils.orientation
import org.hnau.commons.kotlin.foldNullable

@Composable
fun FItem(
    modifier: Modifier = Modifier,
    startAccessory: @Composable (() -> Unit)? = null,
    topAccessory: @Composable (() -> Unit)? = null,
    endAccessory: @Composable (() -> Unit)? = null,
    bottomAccessory: @Composable (() -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    val units = LocalFContext.current.distance.units
    Row(
        modifier = modifier
            .then(
                onClick.foldNullable(
                    ifNull = { Modifier },
                    ifNotNull = { onClickNotNull ->
                        Modifier
                            .clip(units.shape)
                            .clickable(onClick = onClickNotNull)
                    }
                )
            )
            .fPadding(
                spaceSize = SizeType.Medium,
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Accessory(
            side = Side.Start,
            accessory = startAccessory,
        )
        Line(
            modifier = Modifier.weight(1f),
            orientation = Orientation.Vertical,
        ) {
            Accessory(
                side = Side.Top,
                accessory = topAccessory,
            )
            content()
            Accessory(
                side = Side.Bottom,
                accessory = bottomAccessory,
            )
        }
        Accessory(
            side = Side.End,
            accessory = endAccessory,
        )
    }
}

@Composable
private fun Accessory(
    side: Side,
    accessory: @Composable (() -> Unit)?
) {
    val align = side.fold(
        ifStart = { Alignment.CenterEnd },
        ifTop = { Alignment.BottomStart },
        ifEnd = { Alignment.CenterStart },
        ifBottom = { Alignment.TopStart },
    )
    accessory.NullableStateContent(
        contentAlignment = align,
        transitionSpec = TransitionSpec.remember(align, align),
    ) { localAccessory ->
        UpdateFContext(
            update = {
                makeDeeper(
                    offset = 1,
                    resetOverlay = false,
                )
            }
        ) {
            val space = LocalFContext.current.distance.units.padding[side.orientation].small
            Box(
                modifier = side.fold(
                    ifStart = { Modifier.padding(end = space) },
                    ifTop = { Modifier.padding(bottom = space) },
                    ifEnd = { Modifier.padding(start = space) },
                    ifBottom = { Modifier.padding(top = space) }
                ),
            ) {
                localAccessory()
            }
        }
    }
}