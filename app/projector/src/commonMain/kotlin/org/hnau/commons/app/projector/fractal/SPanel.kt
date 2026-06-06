package org.hnau.commons.app.projector.fractal

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import org.hnau.commons.app.model.theme.color.Contrast
import org.hnau.commons.app.projector.fractal.context.LocalFContext
import org.hnau.commons.app.projector.fractal.context.UpdateFContext
import org.hnau.commons.app.projector.fractal.context.color
import org.hnau.commons.app.projector.fractal.context.overlay
import org.hnau.commons.app.projector.fractal.size.units
import org.hnau.commons.app.projector.fractal.utils.BaseWithDecay
import org.hnau.commons.app.projector.fractal.utils.Saturation
import org.hnau.commons.app.projector.fractal.utils.container
import org.hnau.commons.app.projector.fractal.utils.containerLow
import org.hnau.commons.app.projector.fractal.utils.content
import org.hnau.commons.app.projector.fractal.utils.fractalDashBorder
import org.hnau.commons.app.projector.fractal.utils.plus
import org.hnau.commons.app.projector.uikit.rememberActionOrCancel
import org.hnau.commons.app.projector.utils.Orientation
import org.hnau.commons.app.projector.utils.orNoAction
import org.hnau.commons.kotlin.coroutines.ActionOrElse

@Composable
fun SPanel(
    modifier: Modifier = Modifier,
    actionOrElseOrDisabled: ActionOrElse<Unit, *>? = null,
    saturation: Saturation = Saturation.get(actionOrElseOrDisabled != null),
    isSelected: Boolean = false,
    contentOrientation: Orientation = Orientation.Vertical,
    shape: Shape = LocalFContext.current.distance.units.shape,
    contrast: BaseWithDecay<Contrast> = Contrast.containerLow,
    content: @Composable () -> Unit,
) {
    UpdateFContext(
        update = {
            copy(
                saturation = saturation,
            ).overlay(
                contrast = contrast,
            )
        }
    ) {

        val backgroundColor = color

        UpdateFContext(
            update = {
                copy(
                    distance = distance + 1,
                ).overlay(
                    contrast = Contrast.content,
                )
            }
        ) {

            val units = distance.units

            val actionOrCancel = actionOrElseOrDisabled?.rememberActionOrCancel()
            val isInProgress = when (actionOrElseOrDisabled) {
                is ActionOrElse.Else -> true
                is ActionOrElse.Action, null -> false
            }

            Box(
                propagateMinConstraints = true,
                modifier = modifier
                    .clip(shape)
                    .clickable(
                        enabled = actionOrCancel?.onClick != null,
                        onClick = actionOrCancel?.onClick.orNoAction,
                    )
                    .background(backgroundColor)
                    .then(
                        when {
                            isInProgress -> Modifier.fractalDashBorder(
                                color = color,
                                shape = shape,
                            )

                            isSelected -> Modifier.border(
                                width = units.borderWidth,
                                color = color,
                                shape = shape,
                            )

                            else -> Modifier
                        }
                    )
            ) {
                val contentPadding =
                    LocalFContext.current.distance.units.paddingValues[contentOrientation].medium
                Box(
                    modifier = Modifier.padding(contentPadding),
                ) {
                    content()
                }
            }
        }
    }
}
