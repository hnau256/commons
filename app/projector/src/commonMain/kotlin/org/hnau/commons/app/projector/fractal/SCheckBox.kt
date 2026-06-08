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
import org.hnau.commons.app.projector.fractal.context.LocalFContext
import org.hnau.commons.app.projector.fractal.context.color
import org.hnau.commons.app.projector.fractal.context.containerOverlay
import org.hnau.commons.app.projector.fractal.context.contentOverlay
import org.hnau.commons.app.projector.fractal.distance.LocalDistance
import org.hnau.commons.app.projector.fractal.padding.LocalContentPaddingBox
import org.hnau.commons.app.projector.fractal.size.scale
import org.hnau.commons.app.projector.fractal.size.units
import org.hnau.commons.app.projector.fractal.utils.Importance
import org.hnau.commons.app.projector.fractal.utils.ShapeCorners
import org.hnau.commons.app.projector.fractal.utils.activateIfNeed
import org.hnau.commons.app.projector.fractal.utils.rememberFShape
import org.hnau.commons.app.projector.utils.clickableOption
import org.hnau.commons.kotlin.foldBoolean
import org.hnau.commons.kotlin.ifTrue
import androidx.compose.runtime.remember as rememberInComposer

@Composable
fun SCheckBox(
    isChecked: Boolean,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) {
    LocalContentPaddingBox {
        val inactiveState = StateInfo.remember(
            checked = false,
        )

        val activeState = StateInfo.remember(
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
        val separation = units.borderWidth * 3

        val shape = rememberFShape(
            corners = ShapeCorners.Provider.opened,
        )

        Box(
            modifier = modifier
                .clip(shape)
                .border(
                    width = units.borderWidth,
                    color = lerp(
                        start = inactiveState.borderColor,
                        stop = activeState.borderColor,
                        fraction = activePercentage,
                    ),
                    shape = shape,
                )
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
            checked: Boolean,
        ): StateInfo {

            val fContext = LocalFContext.current

            val containerFContext = fContext
                .copy(
                    mood = fContext.mood.activateIfNeed(
                        importance = checked.ifTrue { Importance.default },
                    )
                )
                .containerOverlay()

            val containerColor = containerFContext.color
            val contentColor = containerFContext
                .contentOverlay()
                .color

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

