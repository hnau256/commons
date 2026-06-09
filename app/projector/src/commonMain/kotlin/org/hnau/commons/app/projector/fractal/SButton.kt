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
import org.hnau.commons.kotlin.coroutines.ActionOrElse

@Composable
fun SButton(
    actionOrElseOrDisabled: ActionOrElse<Unit, *>?,
    titleOrIcon: TitleOrIcon,
    modifier: Modifier = Modifier,
    shape: Shape = rememberFShape(),
    importanceToActivate: Importance? = Importance.default,
    isSelected: Boolean = false,
) {
    SPanel(
        actionOrElseOrDisabled = actionOrElseOrDisabled,
        modifier = modifier,
        shape = shape,
        isSelected = isSelected,
        importanceToActivate = importanceToActivate,
        contentOrientation = Orientation.Horizontal,
        contentAlignment = Alignment.Center,
    ) {
        STitleOrIcon(
            titleOrIcon = titleOrIcon.withActionOrElse(
                actionOrElseOrDisabled = actionOrElseOrDisabled,
            ),
        )
    }
}
