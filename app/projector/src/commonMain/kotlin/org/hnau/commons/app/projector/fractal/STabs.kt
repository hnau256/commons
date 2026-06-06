package org.hnau.commons.app.projector.fractal

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.lerp
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import org.hnau.commons.app.model.theme.color.Contrast
import org.hnau.commons.app.projector.fractal.context.FContext
import org.hnau.commons.app.projector.fractal.context.LocalFContext
import org.hnau.commons.app.projector.fractal.context.color
import org.hnau.commons.app.projector.fractal.context.containerOverlay
import org.hnau.commons.app.projector.fractal.context.contentOverlay
import org.hnau.commons.app.projector.fractal.context.overlay
import org.hnau.commons.app.projector.fractal.distance.LocalDistance
import org.hnau.commons.app.projector.fractal.size.units
import org.hnau.commons.app.projector.fractal.utils.Importance
import org.hnau.commons.app.projector.fractal.utils.activateIfNeed
import org.hnau.commons.app.projector.fractal.utils.content
import org.hnau.commons.app.projector.utils.clickableOption
import org.hnau.commons.app.projector.utils.rememberRun
import org.hnau.commons.kotlin.Mutable
import org.hnau.commons.kotlin.foldBoolean
import kotlin.math.ceil
import kotlin.math.floor

@Composable
fun <T> STabs(
    items: List<T>,
    selection: T,
    onClick: ((T) -> Unit)?,
    modifier: Modifier = Modifier,
    importanceToActivate: Importance? = Importance.default,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.SpaceAround,
    itemPaddingValues: PaddingValues = LocalDistance.current.units.paddingValues.horizontal.small,
    item: @Composable (item: T) -> Unit,
) {
    FContext(
        update = { containerOverlay() },
    ) {
        val distance = LocalDistance.current
        val itemMargin = distance.units.borderWidth
        val cornerRadius = distance.units.cornerRadius
        val itemCornerRadius = cornerRadius - itemMargin
        val density = LocalDensity.current

        val selectedFContext = LocalFContext.current.rememberRun {
            copy(
                mood = mood.activateIfNeed(
                    importance = onClick?.let { importanceToActivate },
                ),
            )
        }.contentOverlay()

        val childrenPositions: List<Mutable<Rect>> = remember(items) {
            items.map { Mutable(Rect.Zero) }
        }


        val selectedIndex = items.indexOf(selection).takeIf { it >= 0 } ?: 0
        val animatedSelectedIndex: State<Float> = animateFloatAsState(
            targetValue = selectedIndex.toFloat(),
        )


        val color = LocalFContext.current.color
        Row(
            modifier = modifier
                .height(IntrinsicSize.Max)
                .drawBehind {
                    drawRoundRect(
                        color = color,
                        size = size,
                        cornerRadius = with(density) { cornerRadius.toPx() }.let(::CornerRadius),
                    )

                    val index = animatedSelectedIndex.value
                    val fromIndex = floor(index).toInt()
                    val toIndex = ceil(index).toInt()
                    val fromRect = childrenPositions
                        .getOrNull(fromIndex)
                        ?.value
                        ?: return@drawBehind
                    val rect = (fromIndex == toIndex).foldBoolean(
                        ifTrue = { fromRect },
                        ifFalse = {
                            val toRect = childrenPositions
                                .getOrNull(toIndex)
                                ?.value
                                ?: return@drawBehind
                            lerp(
                                start = fromRect,
                                stop = toRect,
                                fraction = index - fromIndex
                            )
                        }
                    )
                    drawRoundRect(
                        color = selectedFContext.color,
                        topLeft = rect.topLeft + with(density) {
                            itemMargin.toPx().let { offset ->
                                Offset(offset, offset)
                            }
                        },
                        size = rect.size,
                        cornerRadius = with(density) { itemCornerRadius.toPx() }.let(::CornerRadius),
                    )
                }
                .padding(itemMargin),
            horizontalArrangement = horizontalArrangement,
        ) {
            val itemShape = RoundedCornerShape(itemCornerRadius)
            items.forEachIndexed { i, item ->
                val isSelected = i == selectedIndex
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .clip(itemShape)
                        .clickableOption(
                            onClick = onClick
                                .takeIf { !isSelected }
                                ?.let { onClickNotNull ->
                                    { onClickNotNull(item) }
                                }
                        )
                        .onGloballyPositioned { coordinates ->
                            childrenPositions[i].value = coordinates.boundsInParent()
                        }
                        .padding(itemPaddingValues),
                    propagateMinConstraints = true,
                ) {
                    CompositionLocalProvider(
                        LocalFContext provides isSelected.foldBoolean(
                            ifTrue = { selectedFContext },
                            ifFalse = { LocalFContext.current },
                        )
                    ) {
                        item(item)
                    }
                }
            }
        }
    }
}