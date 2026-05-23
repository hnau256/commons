package org.hnau.commons.app.projector.fractal

import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.hnau.commons.app.projector.fractal.context.LocalFContext
import org.hnau.commons.app.projector.fractal.context.UpdateFContext
import org.hnau.commons.app.projector.fractal.size.units
import org.hnau.commons.app.projector.fractal.utils.Mood
import org.hnau.commons.app.projector.uikit.line.Line
import org.hnau.commons.app.projector.uikit.line.weight
import org.hnau.commons.app.projector.utils.Orientation
import org.hnau.commons.app.projector.utils.TitleOrIcon
import org.hnau.commons.kotlin.coroutines.ActionOrElse
import org.hnau.commons.kotlin.coroutines.CancelOrInProgress

@Composable
fun SActions(
    modifier: Modifier = Modifier,
    block: @Composable SActionsScope.() -> Unit,
) {
    val fContext = LocalFContext.current
    Line(
        modifier = modifier,
        orientation = when (fContext.distance.distance) {
            0 -> Orientation.Vertical
            else -> Orientation.Horizontal
        },
        reverseOrdering = true,
        separation = fContext.distance.units.padding.along.small,
    ) {
        SActionsScope.block()
        Spacer(Modifier.weight(1f))
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