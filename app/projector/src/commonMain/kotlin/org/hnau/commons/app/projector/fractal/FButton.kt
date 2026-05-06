package org.hnau.commons.app.projector.fractal

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow
import org.hnau.commons.app.model.theme.palette.PaletteType
import org.hnau.commons.app.projector.fractal.utils.LocalDistance
import org.hnau.commons.app.projector.fractal.utils.SwitchPalette
import org.hnau.commons.app.projector.fractal.utils.color.localBackground
import org.hnau.commons.app.projector.fractal.utils.color.localContent
import org.hnau.commons.app.projector.fractal.utils.color.tone.SwitchBackgroundToneToContainer
import org.hnau.commons.app.projector.fractal.utils.fractalDashBorder
import org.hnau.commons.app.projector.fractal.utils.orInactive
import org.hnau.commons.app.projector.fractal.utils.preview.FractalPreview
import org.hnau.commons.app.projector.fractal.utils.size.units
import org.hnau.commons.app.projector.uikit.ActionOrCancel
import org.hnau.commons.app.projector.uikit.rememberActionOrCancel
import org.hnau.commons.app.projector.uikit.state.StateContent
import org.hnau.commons.app.projector.uikit.transition.TransitionSpec
import org.hnau.commons.app.projector.utils.Drawable
import org.hnau.commons.app.projector.utils.Orientation
import org.hnau.commons.app.projector.utils.TitleOrIcon
import org.hnau.commons.app.projector.utils.iconOrNull
import org.hnau.commons.app.projector.utils.orNoAction
import org.hnau.commons.app.projector.utils.titleOrNull
import org.hnau.commons.kotlin.coroutines.ActionOrElse
import org.hnau.commons.kotlin.coroutines.CancelOrInProgress
import org.hnau.commons.kotlin.coroutines.actionOrCancelIfExecuting
import org.hnau.commons.kotlin.foldNullable
import kotlin.time.Duration.Companion.seconds

@Composable
fun <E : CancelOrInProgress> FButton(
    actionOrElseOrDisabled: ActionOrElse<Unit, E>?,
    titleOrIcon: TitleOrIcon,
    palette: PaletteType = PaletteType.default,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
) {
    SwitchPalette(
        newPalette = palette.orInactive(
            active = actionOrElseOrDisabled != null,
        ),
    ) {
        SwitchBackgroundToneToContainer {

            val actionOrCancel = actionOrElseOrDisabled?.rememberActionOrCancel

            val units = LocalDistance.current.units

            val isInProgress = when (actionOrElseOrDisabled) {
                is ActionOrElse.Else -> true
                is ActionOrElse.Action, null -> false
            }

            Row(
                modifier = modifier
                    .clip(units.shape)
                    .clickable(
                        enabled = actionOrCancel?.onClick != null,
                        onClick = actionOrCancel?.onClick.orNoAction,
                    )
                    .background(Color.localBackground)
                    .then(
                        when {
                            isInProgress -> Modifier.fractalDashBorder(
                                color = Color.localContent,
                                shape = units.borderShape,
                            )

                            isSelected -> Modifier.border(
                                width = units.borderWidth,
                                color = Color.localContent,
                                shape = units.borderShape,
                            )

                            else -> Modifier
                        }
                    )
                    .padding(
                        horizontal = units.padding.horizontal.medium,
                    )
                    .height(
                        units.iconSize + units.padding.vertical.medium * 2,
                    ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                val titleOrNull = titleOrIcon.titleOrNull
                val actionIconOrNull = titleOrIcon.iconOrNull
                val iconOrNull = remember(actionOrCancel, actionIconOrNull) {
                    val type = actionOrCancel?.type
                    when (type) {
                        ActionOrCancel.Type.Cancel -> Drawable.Vector(Icons.Default.Clear)
                        ActionOrCancel.Type.Action, null -> actionIconOrNull
                    }
                }
                iconOrNull
                    .StateContent(
                        label = "iconOrCancel",
                        contentKey = { it },
                        transitionSpec = titleOrNull.foldNullable(
                            ifNull = { TransitionSpec.rememberCenter() },
                            ifNotNull = {
                                TransitionSpec.remember(
                                    Alignment.CenterStart,
                                    Alignment.CenterStart,
                                )
                            }
                        ),
                    ) { iconOrNullLocal ->
                        iconOrNullLocal?.let { drawable ->
                            FIcon(
                                drawable = drawable,
                                modifier = titleOrNull.foldNullable(
                                    ifNull = { Modifier },
                                    ifNotNull = {
                                        Modifier.padding(
                                            end = units.padding.horizontal.extraSmall,
                                        )
                                    }
                                )
                            )
                        }
                    }
                titleOrNull?.let { title ->
                    FText(
                        text = title,
                        maxLines = 1,
                    )
                }
            }
        }
    }
}

@Preview(
    widthDp = 480
)
@Composable
fun FButtonPreview() {

    val coroutineScope = rememberCoroutineScope()

    val createActionOrCancel: @Composable () -> StateFlow<ActionOrElse<Unit, CancelOrInProgress>> =
        {
            remember(coroutineScope) {
                actionOrCancelIfExecuting(
                    scope = coroutineScope,
                ) {
                    delay(50.seconds)
                }

            }
        }

    FractalPreview {
        FLine(
            orientation = Orientation.Vertical,
        ) {
            FText(
                text = "Lorem ipsum dolor sit amet",
            )
            FLine(
                orientation = Orientation.Horizontal,
            ) {
                FButton(
                    palette = PaletteType.Secondary,
                    actionOrElseOrDisabled = createActionOrCancel().collectAsState().value,
                    titleOrIcon = TitleOrIcon.Icon(
                        icon = Drawable.Vector(Icons.Default.Delete),
                    )
                )
                FButton(
                    palette = PaletteType.Primary,
                    actionOrElseOrDisabled = createActionOrCancel().collectAsState().value,
                    isSelected = true,
                    titleOrIcon = TitleOrIcon.Both(
                        title = "Settings",
                        icon = Drawable.Vector(Icons.Default.Settings),
                    ),
                    /*titleOrIcon = Ior.Left(
                    value = "Settings",
                ),*/
                )
            }
        }
    }
}


