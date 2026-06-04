package org.hnau.commons.app.projector.fractal

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import org.hnau.commons.app.projector.fractal.context.LocalFContext
import org.hnau.commons.app.projector.fractal.context.UpdateFContext
import org.hnau.commons.app.projector.fractal.context.containerColor
import org.hnau.commons.app.projector.fractal.context.contentColor
import org.hnau.commons.app.projector.fractal.size.units
import org.hnau.commons.app.projector.fractal.utils.Mood
import org.hnau.commons.app.projector.fractal.utils.Saturation
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
    isSelected: Boolean = false,
    contentOrientation: Orientation = Orientation.Vertical,
    content: @Composable () -> Unit,
) {
    val fContext = LocalFContext.current
    val units = fContext.distance.units
    val foregroundColor = fContext.contentColor
    val shape = fContext.distance.units.shape

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
            .background(fContext.containerColor)
            .then(
                when {
                    isInProgress -> Modifier.fractalDashBorder(
                        color = foregroundColor,
                        shape = shape,
                    )

                    isSelected -> Modifier.border(
                        width = units.borderWidth,
                        color = foregroundColor,
                        shape = shape,
                    )

                    else -> Modifier
                }
            )
    ) {
        UpdateFContext(
            update = {
                copy(
                    distance = distance + 1,
                    saturation = Saturation.Neutral,
                    mood = Mood.default,
                    customContainerTone = null,
                )
            }
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
