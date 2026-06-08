package org.hnau.commons.app.projector.fractal

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import org.hnau.commons.app.projector.fractal.utils.Importance
import org.hnau.commons.app.projector.fractal.utils.rememberFShape
import org.hnau.commons.app.projector.fractal.utils.withActionOrElse
import org.hnau.commons.app.projector.utils.Orientation
import org.hnau.commons.app.projector.utils.TitleOrIcon
import org.hnau.commons.app.projector.utils.iconOrNull
import org.hnau.commons.app.projector.utils.titleOrNull
import org.hnau.commons.kotlin.coroutines.ActionOrElse

@Composable
fun SButton(
    actionOrElseOrDisabled: ActionOrElse<Unit, *>?,
    titleOrIcon: TitleOrIcon,
    modifier: Modifier = Modifier,
    shape: Shape = rememberFShape(),
    importance: Importance = Importance.default,
    isSelected: Boolean = false,
) {
    SPanel(
        actionOrElseOrDisabled = actionOrElseOrDisabled,
        modifier = modifier,
        shape = shape,
        isSelected = isSelected,
        importanceToActivate = importance,
        contentOrientation = Orientation.Horizontal,
        contentAlignment = Alignment.Center,
    ) {
        val titleOrIcon = titleOrIcon.withActionOrElse(
            actionOrElseOrDisabled = actionOrElseOrDisabled,
        )
        SItem(
            startAccessory = titleOrIcon.iconOrNull?.let { icon ->
                { SIcon(icon) }
            },
            content = titleOrIcon.titleOrNull?.let { title ->
                { SText(title) }
            },
            expandHorizontally = false,
        )
    }
}
