package org.hnau.commons.app.projector.fractal

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.hnau.commons.app.projector.fractal.distance.DistanceOffset
import org.hnau.commons.app.projector.fractal.distance.LocalDistance
import org.hnau.commons.app.projector.fractal.padding.LocalContentPadding
import org.hnau.commons.app.projector.fractal.size.SizeType
import org.hnau.commons.app.projector.fractal.size.units
import org.hnau.commons.app.projector.uikit.line.Line
import org.hnau.commons.app.projector.uikit.state.NullableStateContent
import org.hnau.commons.app.projector.uikit.state.StateContent
import org.hnau.commons.app.projector.uikit.transition.TransitionSpec
import org.hnau.commons.app.projector.utils.Orientation
import org.hnau.commons.app.projector.utils.Side
import org.hnau.commons.app.projector.utils.bottom
import org.hnau.commons.app.projector.utils.copy
import org.hnau.commons.app.projector.utils.fold
import org.hnau.commons.app.projector.utils.option
import org.hnau.commons.app.projector.utils.plus
import org.hnau.commons.app.projector.utils.top
import org.hnau.commons.kotlin.foldNullable
import org.hnau.commons.kotlin.ifTrue

@Composable
fun SItem(
    modifier: Modifier = Modifier,
    startAccessory: @Composable (() -> Unit)? = null,
    topAccessory: @Composable (() -> Unit)? = null,
    endAccessory: @Composable (() -> Unit)? = null,
    bottomAccessory: @Composable (() -> Unit)? = null,
    expandHorizontally: Boolean = true,
    content: (@Composable () -> Unit)?,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val contentPadding = LocalContentPadding.current
        Accessory(
            side = Side.Start,
            accessory = startAccessory,
            distanceOffset = 0,
            contentPadding = contentPadding.copy(
                end = 0.dp,
            ),
        )
        Line(
            modifier = Modifier.option(
                expandHorizontally.ifTrue {
                    Modifier.weight(1f)
                }
            ),
            orientation = Orientation.Vertical,
        ) {
            Accessory(
                side = Side.Top,
                accessory = topAccessory,
                distanceOffset = 1,
                contentPadding = PaddingValues(
                    top = contentPadding.top,
                ),
            )
            content.NullableStateContent(
                transitionSpec = TransitionSpec.rememberCenter(),
            ) { contentNotNull ->
                contentNotNull()
            }
            Accessory(
                side = Side.Bottom,
                accessory = bottomAccessory,
                distanceOffset = 1,
                contentPadding = PaddingValues(
                    bottom = contentPadding.bottom,
                ),
            )
        }
        Accessory(
            side = Side.End,
            accessory = endAccessory,
            distanceOffset = 0,
            contentPadding = contentPadding.copy(
                start = 0.dp,
            ),
        )
    }
}

@Composable
private fun Accessory(
    side: Side,
    distanceOffset: Int,
    contentPadding: PaddingValues,
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
        transitionSpec = TransitionSpec.remember(
            showAlignment = align,
            hideAlignment = align,
        ),
        label = "Accessory${side}OrPlaceholder",
        contentKey = { it != null },
    ) { localAccessoryOrNull ->
        localAccessoryOrNull.foldNullable(
            ifNull = {
                Box(
                    modifier = Modifier.padding(
                        paddingValues = contentPadding,
                    )
                )
            },
            ifNotNull = { localAccessory ->
                DistanceOffset(distanceOffset) {
                    val units = LocalDistance.current.units
                    val paddingSize = SizeType.Small
                    val accessoryPadding = contentPadding + side.fold(
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
                    CompositionLocalProvider(
                        value = LocalContentPadding provides accessoryPadding,
                    ) {
                        localAccessory()
                    }
                }
            }
        )
    }
}
