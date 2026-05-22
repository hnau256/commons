package org.hnau.commons.app.projector.fractal

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import org.hnau.commons.app.projector.fractal.context.LocalFContext
import org.hnau.commons.app.projector.fractal.context.UpdateFContext
import org.hnau.commons.app.projector.fractal.context.containerColor
import org.hnau.commons.app.projector.fractal.context.contentColor
import org.hnau.commons.app.projector.fractal.context.overlay
import org.hnau.commons.app.projector.fractal.size.units
import org.hnau.commons.app.projector.fractal.utils.LocalSContentBox
import org.hnau.commons.app.projector.fractal.utils.LocalSContentPadding
import org.hnau.commons.app.projector.fractal.utils.Saturation
import org.hnau.commons.app.projector.fractal.utils.fractalDashBorder
import org.hnau.commons.app.projector.uikit.ActionOrCancel
import org.hnau.commons.app.projector.uikit.rememberActionOrCancel
import org.hnau.commons.app.projector.utils.Drawable
import org.hnau.commons.app.projector.utils.TitleOrIcon
import org.hnau.commons.app.projector.utils.fold
import org.hnau.commons.app.projector.utils.iconOrNull
import org.hnau.commons.app.projector.utils.orNoAction
import org.hnau.commons.kotlin.coroutines.ActionOrElse
import org.hnau.commons.kotlin.coroutines.CancelOrInProgress
import org.hnau.commons.kotlin.foldNullable

@Composable
fun <E : CancelOrInProgress> SButton(
    actionOrElseOrDisabled: ActionOrElse<Unit, E>?,
    titleOrIcon: TitleOrIcon,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
) {
    LocalSContentBox(
        modifier = modifier,
        propagateMinConstraints = true,
    ) {
        UpdateFContext(
            update = {
                copy(
                    saturation = Saturation.get(actionOrElseOrDisabled != null)
                ).overlay()
            }
        ) {

            val actionOrCancel = actionOrElseOrDisabled?.rememberActionOrCancel()


            val isInProgress = when (actionOrElseOrDisabled) {
                is ActionOrElse.Else -> true
                is ActionOrElse.Action, null -> false
            }

            val fContext = LocalFContext.current
            val units = fContext.distance.units
            val foregroundColor = fContext.contentColor
            Box(
                propagateMinConstraints = true,
                modifier = Modifier
                    .clip(units.shape)
                    .clickable(
                        enabled = actionOrCancel?.onClick != null,
                        onClick = actionOrCancel?.onClick.orNoAction,
                    )
                    .background(fContext.containerColor)
                    .then(
                        when {
                            isInProgress -> Modifier.fractalDashBorder(
                                color = foregroundColor,
                                shape = units.borderShape,
                            )

                            isSelected -> Modifier.border(
                                width = units.borderWidth,
                                color = foregroundColor,
                                shape = units.borderShape,
                            )

                            else -> Modifier
                        }
                    ),
            ) {
                CompositionLocalProvider(
                    LocalSContentPadding provides units.paddingValues.horizontal.medium,
                ) {
                    STitleOrIcon(
                        titleOrIcon = remember(actionOrCancel, titleOrIcon) {

                            val type = actionOrCancel?.type
                            val iconToOverwrite = when (type) {
                                ActionOrCancel.Type.Cancel -> Drawable.Vector(Icons.Default.Clear)
                                ActionOrCancel.Type.Action, null -> titleOrIcon.iconOrNull
                            }

                            titleOrIcon.fold(
                                ifTitle = { title ->
                                    iconToOverwrite.foldNullable(
                                        ifNull = { TitleOrIcon.Title(title) },
                                        ifNotNull = { icon -> TitleOrIcon.Both(title, icon) },
                                    )
                                },
                                ifIcon = { icon ->
                                    TitleOrIcon.Icon(iconToOverwrite ?: icon)
                                },
                                ifBoth = { title, icon ->
                                    TitleOrIcon.Both(
                                        title = title,
                                        icon = iconToOverwrite ?: icon,
                                    )
                                }
                            )
                        },
                    )
                }
            }
        }
    }
}