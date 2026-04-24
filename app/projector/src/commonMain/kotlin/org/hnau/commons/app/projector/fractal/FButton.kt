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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.tooling.preview.Preview
import arrow.core.Ior
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow
import org.hnau.commons.app.projector.fractal.utils.color.FractalColorsProvider
import org.hnau.commons.app.projector.fractal.utils.color.PaletteType
import org.hnau.commons.app.projector.fractal.utils.color.getComponentColors
import org.hnau.commons.app.projector.fractal.utils.color.local
import org.hnau.commons.app.projector.fractal.utils.fractalDashBorder
import org.hnau.commons.app.projector.fractal.utils.localBorderShape
import org.hnau.commons.app.projector.fractal.utils.localBorderWidth
import org.hnau.commons.app.projector.fractal.utils.localIconSize
import org.hnau.commons.app.projector.fractal.utils.localPaddingHorizontal
import org.hnau.commons.app.projector.fractal.utils.localShape
import org.hnau.commons.app.projector.fractal.utils.localTextStyle
import org.hnau.commons.app.projector.fractal.utils.padding
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

    val colorsProvider = FractalColorsProvider.local
    val colors = colorsProvider.getComponentColors(
        palette = palette,
    )
    val onClick = actionOrElseOrDisabled?.onClick

    val isInProgress = when (actionOrElseOrDisabled) {
        is ActionOrElse.Else -> true
        is ActionOrElse.Action, null -> false
    }

    Row(
        modifier = modifier
            .clip(localShape)
            .clickable(
                enabled = onClick != null,
                onClick = onClick.orNoAction,
            )
            .background(colors.container)
            .then(
                when {
                    isInProgress -> Modifier.fractalDashBorder(
                        color = colors.content,
                        shape = localBorderShape,
                    )

                    isSelected -> Modifier.border(
                        width = localBorderWidth,
                        color = colors.content,
                        shape = localBorderShape,
                    )

                    else -> Modifier
                }
            )
            .padding(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val titleOrNull = titleOrIcon.leftOrNull()
        ActionOrElseIcon(
            size = localIconSize,
            actionOrElseOrDisabled = actionOrElseOrDisabled,
            actionIcon = titleOrIcon.rightOrNull()?.let { icon ->
                {
                    Box(
                        modifier = Modifier.paint(
                            painter = rememberVectorPainter(icon),
                            colorFilter = ColorFilter.tint(colors.content),
                        )
                    )
                }
            },
            contentPadding = titleOrNull.foldNullable(
                ifNull = { PaddingValuesZero },
                ifNotNull = {
                    PaddingValues(
                        end = localPaddingHorizontal / 2,
                    )
                }
            ),
        )
        titleOrNull?.let { title ->
            Text(
                text = title,
                style = localTextStyle,
                color = colors.content,
                maxLines = 1,
            )
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


