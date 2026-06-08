package org.hnau.commons.app.projector.fractal

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import org.hnau.commons.app.projector.fractal.context.FContext
import org.hnau.commons.app.projector.fractal.context.LocalFContext
import org.hnau.commons.app.projector.fractal.context.color
import org.hnau.commons.app.projector.fractal.context.containerOverlay
import org.hnau.commons.app.projector.fractal.context.contentOverlay
import org.hnau.commons.app.projector.fractal.distance.DistanceOffset
import org.hnau.commons.app.projector.fractal.distance.LocalDistance
import org.hnau.commons.app.projector.fractal.padding.LocalContentPaddingBox
import org.hnau.commons.app.projector.fractal.size.units
import org.hnau.commons.app.projector.fractal.utils.Importance
import org.hnau.commons.app.projector.fractal.utils.LocalShapeCorners
import org.hnau.commons.app.projector.fractal.utils.ShapeCorners
import org.hnau.commons.app.projector.fractal.utils.activateIfNeed
import org.hnau.commons.app.projector.fractal.utils.fractalDashBorder
import org.hnau.commons.app.projector.fractal.utils.rememberFShape
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
    shape: Shape = rememberFShape(),
    importanceToActivate: Importance? = Importance.default,
    contentAlignment: Alignment = Alignment.TopStart,
    content: @Composable () -> Unit,
) {
    LocalContentPaddingBox {
        FContext(
            update = {
                copy(
                    mood = mood.activateIfNeed(
                        importance = actionOrElseOrDisabled?.let { importanceToActivate },
                    ),
                ).containerOverlay()
            }
        ) {

            val backgroundColor = LocalFContext.current.color

            DistanceOffset {

                val units = LocalDistance.current.units

                val actionOrCancel = actionOrElseOrDisabled?.rememberActionOrCancel()
                val isInProgress = when (actionOrElseOrDisabled) {
                    is ActionOrElse.Else -> true
                    is ActionOrElse.Action, null -> false
                }

                val foregroundColor = LocalFContext.current.contentOverlay().color

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
                        ),
                ) {
                    Box(
                        modifier = Modifier.padding(
                            paddingValues = LocalDistance.current
                                .units
                                .paddingValues[contentOrientation]
                                .medium
                        ),
                        contentAlignment = contentAlignment,
                    ) {
                        CompositionLocalProvider(
                            value = LocalShapeCorners provides ShapeCorners.Provider.opened,
                            content = content,
                        )
                    }
                }
            }
        }
    }
}
