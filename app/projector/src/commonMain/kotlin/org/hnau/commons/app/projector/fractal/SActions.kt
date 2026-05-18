package org.hnau.commons.app.projector.fractal

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.hnau.commons.app.projector.fractal.context.LocalFContext
import org.hnau.commons.app.projector.fractal.context.UpdateFContext
import org.hnau.commons.app.projector.fractal.size.SizeType
import org.hnau.commons.app.projector.fractal.utils.Mood
import org.hnau.commons.app.projector.utils.Orientation
import org.hnau.commons.app.projector.utils.TitleOrIcon
import org.hnau.commons.kotlin.coroutines.ActionOrElse
import org.hnau.commons.kotlin.coroutines.CancelOrInProgress

@Composable
fun SActions(
    modifier: Modifier = Modifier,
    block: @Composable SActionsScope.() -> Unit,
) {
    SLine(
        modifier = modifier,
        orientation = when (LocalFContext.current.distance.distance) {
            0 -> Orientation.Vertical
            else -> Orientation.Horizontal
        },
        reverseOrdering = true,
        alignment = Alignment.End,
        separation = SizeType.Small,
    ) {
        SActionsScope.block()
    }
}

object SActionsScope {

    @Composable
    fun <E : CancelOrInProgress> Action(
        actionOrElseOrDisabled: ActionOrElse<Unit, E>?,
        titleOrIcon: TitleOrIcon,
        mood: Mood = Mood.Primary,
    ) {
        UpdateFContext(
            update = { copy(mood = mood) }
        ) {
            SButton(
                actionOrElseOrDisabled = actionOrElseOrDisabled,
                titleOrIcon = titleOrIcon,
            )
        }
    }
}