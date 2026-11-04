package org.hnau.commons.app.projector.fractal

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import org.hnau.commons.app.model.theme.color.Contrast
import org.hnau.commons.app.projector.fractal.context.FContext
import org.hnau.commons.app.projector.fractal.context.LocalFContext
import org.hnau.commons.app.projector.fractal.context.color
import org.hnau.commons.app.projector.fractal.context.contentOverlay
import org.hnau.commons.app.projector.fractal.context.overlay
import org.hnau.commons.app.projector.fractal.distance.DistanceOffset
import org.hnau.commons.app.projector.fractal.distance.LocalDistance
import org.hnau.commons.app.projector.fractal.padding.LocalContentPadding
import org.hnau.commons.app.projector.fractal.padding.LocalContentPaddingBox
import org.hnau.commons.app.projector.fractal.size.units
import org.hnau.commons.app.projector.fractal.utils.BaseWithDecay
import org.hnau.commons.app.projector.fractal.utils.Importance
import org.hnau.commons.app.projector.fractal.utils.LocalShapeCorners
import org.hnau.commons.app.projector.fractal.utils.SaturationValues
import org.hnau.commons.app.projector.fractal.utils.ShapeCorners
import org.hnau.commons.app.projector.fractal.utils.activate
import org.hnau.commons.app.projector.fractal.utils.container
import org.hnau.commons.app.projector.fractal.utils.fractalDashBorder
import org.hnau.commons.app.projector.fractal.utils.rememberFShape
import org.hnau.commons.app.projector.uikit.rememberActionOrCancel
import org.hnau.commons.app.projector.utils.Orientation
import org.hnau.commons.app.projector.utils.orNoAction
import org.hnau.commons.kotlin.coroutines.ActionOrElse
import org.hnau.commons.kotlin.foldBoolean
import org.hnau.commons.kotlin.foldNullable

@Composable
fun SPanel(
    modifier: Modifier = Modifier,
    actionOrElseOrDisabled: ActionOrElse<Unit, *>? = null,
    contentOrientation: Orientation = Orientation.Vertical,
    shape: Shape = rememberFShape(),
    importanceToActivate: Importance? = Importance.default,
    contrast: SaturationValues<BaseWithDecay<Contrast>> = Contrast.container,
    content: @Composable () -> Unit,
) {
    LocalContentPaddingBox {
        FContext(
            update = {
                copy(
                    mood = importanceToActivate
                        .takeIf { actionOrElseOrDisabled != null }
                        .foldNullable(
                            ifNull = { mood },
                            ifNotNull = mood::activate,
                        ),
                ).overlay(
                    contrast = contrast,
                )
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
                            isInProgress.foldBoolean(
                                ifTrue = {
                                    Modifier.fractalDashBorder(
                                        color = foregroundColor,
                                        shape = shape,
                                    )
                                },
                                ifFalse = { Modifier },
                            )
                        ),
                ) {
                    val contentPadding = units
                        .paddingValues[contentOrientation]
                        .medium
                    CompositionLocalProvider(
                        LocalContentPadding provides contentPadding,
                        LocalShapeCorners provides ShapeCorners.Provider.opened,
                        content = content,
                    )
                }
            }
        }
    }
}
