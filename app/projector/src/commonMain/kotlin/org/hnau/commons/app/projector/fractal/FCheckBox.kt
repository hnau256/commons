package org.hnau.commons.app.projector.fractal

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.hnau.commons.app.model.theme.palette.PaletteType
import org.hnau.commons.app.projector.fractal.utils.LocalDistance
import org.hnau.commons.app.projector.fractal.utils.color.ColorType
import org.hnau.commons.app.projector.fractal.utils.size.scale
import org.hnau.commons.app.projector.fractal.utils.size.units
import org.hnau.commons.app.projector.utils.clickableOption
import org.hnau.commons.app.projector.utils.theme.getLocalColor
import org.hnau.commons.kotlin.foldBoolean
import androidx.compose.runtime.remember as rememberInComposer

@Composable
fun FCheckBox(
    isChecked: Boolean,
    modifier: Modifier = Modifier,
    palette: PaletteType = PaletteType.default,
    onClick: (() -> Unit)? = null,
) {

    val inactiveState = StateInfo.remember(
        palette = palette,
        checked = false,
    )

    val activeState = StateInfo.remember(
        palette = palette,
        checked = true,
    )

    val activePercentage: Float by animateFloatAsState(
        isChecked.foldBoolean(
            ifTrue = { 1f },
            ifFalse = { 0f },
        )
    )

    val distance = LocalDistance.current
    val units = distance.units
    val handleSize = 24.dp.scale(distance.scale.space)
    val maxOffset = activeState.handleOffset - inactiveState.handleOffset
    val separation = units.padding.vertical.small

    Box(
        modifier = modifier
            .border(
                width = units.borderWidth,
                color = lerp(
                    start = inactiveState.borderColor,
                    stop = activeState.borderColor,
                    fraction = activePercentage,
                ),
                shape = units.borderShape,
            )
            .clip(units.shape)
            .clickableOption(onClick)
            .background(
                color = lerp(
                    start = inactiveState.containerColor,
                    stop = activeState.containerColor,
                    fraction = activePercentage,
                ),
            )
            .padding(separation)
            .size(
                width = maxOffset + handleSize,
                height = handleSize,
            ),
        contentAlignment = Alignment.CenterStart,
    ) {
        val maxOffsetPx = with(LocalDensity.current) { maxOffset.toPx() }
        Box(
            modifier = Modifier
                .graphicsLayer {
                    translationX = maxOffsetPx * activePercentage
                }
                .size(handleSize)
                .background(
                    color = lerp(
                        start = inactiveState.contentColor,
                        stop = activeState.contentColor,
                        fraction = activePercentage,
                    ),
                    shape = RoundedCornerShape(
                        size = units.cornerRadius - separation,
                    )
                )
        )
    }

}

private data class StateInfo(
    val borderColor: Color,
    val containerColor: Color,
    val contentColor: Color,
    val handleOffset: Dp
) {

    companion object {

        @Composable
        fun remember(
            palette: PaletteType,
            checked: Boolean,
        ): StateInfo {
            val paletteOrInactive = checked.foldBoolean(
                ifTrue = { palette },
                ifFalse = { PaletteType.Neutral },
            )
            val containerColor = getLocalColor(
                type = ColorType.Background,
                palette = paletteOrInactive,
            )
            val contentColor = getLocalColor(
                type = ColorType.Content,
                palette = paletteOrInactive,
            )
            val distance = LocalDistance.current
            return rememberInComposer(
                containerColor,
                contentColor,
                checked,
                distance,
            ) {
                StateInfo(
                    borderColor = contentColor.copy(
                        alpha = checked.foldBoolean(
                            ifTrue = { contentColor.alpha },
                            ifFalse = { 0f },
                        )
                    ),
                    contentColor = contentColor,
                    containerColor = containerColor,
                    handleOffset = checked.foldBoolean(
                        ifTrue = { 24.dp.scale(distance.scale.space) },
                        ifFalse = { 0.dp },
                    )
                )
            }
        }
    }
}

