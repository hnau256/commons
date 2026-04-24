package org.hnau.commons.app.projector.fractal

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.tooling.preview.Preview
import arrow.core.Ior
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow
import org.hnau.commons.app.projector.fractal.utils.SwitchBackgroundToneToContainer
import org.hnau.commons.app.projector.fractal.utils.SwitchPalette
import org.hnau.commons.app.projector.fractal.utils.color.PaletteType
import org.hnau.commons.app.projector.fractal.utils.color.localBackground
import org.hnau.commons.app.projector.fractal.utils.color.localContent
import org.hnau.commons.app.projector.fractal.utils.fractalDashBorder
import org.hnau.commons.app.projector.fractal.utils.fractalPadding
import org.hnau.commons.app.projector.fractal.utils.localUnits
import org.hnau.commons.app.projector.fractal.utils.preview.FractalPreview
import org.hnau.commons.app.projector.uikit.ActionOrElseIcon
import org.hnau.commons.app.projector.uikit.onClick
import org.hnau.commons.app.projector.utils.PaddingValuesZero
import org.hnau.commons.app.projector.utils.TitleOrIcon
import org.hnau.commons.app.projector.utils.orNoAction
import org.hnau.commons.kotlin.coroutines.ActionOrElse
import org.hnau.commons.kotlin.coroutines.CancelOrInProgress
import org.hnau.commons.kotlin.coroutines.actionOrCancelIfExecuting
import org.hnau.commons.kotlin.foldNullable
import org.hnau.commons.kotlin.rightOrNull
import kotlin.time.Duration.Companion.seconds

@Composable
fun <E : CancelOrInProgress> FButton(
    actionOrElseOrDisabled: ActionOrElse<Unit, E>?,
    titleOrIcon: TitleOrIcon,
    palette: PaletteType,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
) {
    SwitchPalette(
        newPalette = palette,
    ) {
        SwitchBackgroundToneToContainer {
            val onClick = actionOrElseOrDisabled?.onClick
            val units = localUnits

            val isInProgress = when (actionOrElseOrDisabled) {
                is ActionOrElse.Else -> true
                is ActionOrElse.Action, null -> false
            }

            Row(
                modifier = modifier
                    .clip(units.shape)
                    .clickable(
                        enabled = onClick != null,
                        onClick = onClick.orNoAction,
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
                    .fractalPadding(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                val titleOrNull = titleOrIcon.leftOrNull()
                ActionOrElseIcon(
                    size = units.iconSize,
                    actionOrElseOrDisabled = actionOrElseOrDisabled,
                    actionIcon = titleOrIcon.rightOrNull()?.let { icon ->
                        {
                            Box(
                                modifier = Modifier.paint(
                                    painter = rememberVectorPainter(icon),
                                    colorFilter = ColorFilter.tint(Color.localContent),
                                )
                            )
                        }
                    },
                    contentPadding = titleOrNull.foldNullable(
                        ifNull = { PaddingValuesZero },
                        ifNotNull = {
                            PaddingValues(
                                end = units.paddingHorizontal / 2,
                            )
                        }
                    ),
                )
                titleOrNull?.let { title ->
                    Text(
                        text = title,
                        style = units.textStyle,
                        color = Color.localContent,
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

    val createActionOrCancel: @Composable () -> StateFlow<ActionOrElse<Unit, CancelOrInProgress.Cancel>> =
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
        FRow {
            FButton(
                palette = PaletteType.Tertiary,
                actionOrElseOrDisabled = createActionOrCancel().collectAsState().value,
                titleOrIcon = Ior.Right(
                    value = Icons.Default.Delete
                )
            )
            FButton(
                palette = PaletteType.Primary,
                actionOrElseOrDisabled = createActionOrCancel().collectAsState().value,
                isSelected = true,
                titleOrIcon = Ior.Both(
                    leftValue = "Settings",
                    rightValue = Icons.Default.Settings
                )
            )
        }
    }
}


