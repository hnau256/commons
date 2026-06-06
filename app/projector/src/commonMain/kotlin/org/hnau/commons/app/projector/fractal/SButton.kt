package org.hnau.commons.app.projector.fractal

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import org.hnau.commons.app.model.theme.color.Contrast
import org.hnau.commons.app.projector.fractal.context.LocalFContext
import org.hnau.commons.app.projector.fractal.size.units
import org.hnau.commons.app.projector.fractal.utils.container
import org.hnau.commons.app.projector.fractal.utils.withActionOrElse
import org.hnau.commons.app.projector.utils.Orientation
import org.hnau.commons.app.projector.utils.TitleOrIcon
import org.hnau.commons.kotlin.coroutines.ActionOrElse

@Composable
fun SButton(
    actionOrElseOrDisabled: ActionOrElse<Unit, *>?,
    titleOrIcon: TitleOrIcon,
    modifier: Modifier = Modifier,
    shape: Shape = LocalFContext.current.distance.units.shape,
    isSelected: Boolean = false,
) {
    SPanel(
        actionOrElseOrDisabled = actionOrElseOrDisabled,
        modifier = modifier,
        shape = shape,
        isSelected = isSelected,
        contentOrientation = Orientation.Horizontal,
        contrast = Contrast.container,
    ) {
        STitleOrIcon(
            titleOrIcon = titleOrIcon.withActionOrElse(
                actionOrElseOrDisabled = actionOrElseOrDisabled,
            )
        )
    }
}
