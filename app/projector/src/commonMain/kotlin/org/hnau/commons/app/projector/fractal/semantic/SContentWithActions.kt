package org.hnau.commons.app.projector.fractal.semantic

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.hnau.commons.app.projector.fractal.FButton
import org.hnau.commons.app.projector.fractal.FLine
import org.hnau.commons.app.projector.fractal.ForceFill
import org.hnau.commons.app.projector.fractal.semantic.utils.Importance
import org.hnau.commons.app.projector.fractal.semantic.utils.palette
import org.hnau.commons.app.projector.fractal.utils.Distance
import org.hnau.commons.app.projector.fractal.utils.local
import org.hnau.commons.app.projector.utils.Orientation
import org.hnau.commons.app.projector.utils.TitleOrIcon
import org.hnau.commons.kotlin.coroutines.ActionOrElse
import org.hnau.commons.kotlin.coroutines.CancelOrInProgress

@Composable
fun SContentWithActions(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
    actions: @Composable SActionsScope.() -> Unit,
) {
    FLine(
        modifier = modifier,
        orientation = Orientation.Vertical,
        forceFill = ForceFill.First,
    ) {
        content()
        SActions(
            modifier = Modifier.fillMaxWidth(),
            block = actions,
        )
    }
}

@Composable
fun SActions(
    modifier: Modifier = Modifier,
    block: @Composable SActionsScope.() -> Unit,
) {
    FLine(
        modifier = modifier,
        orientation = when (Distance.local.distance) {
            0 -> Orientation.Vertical
            else -> Orientation.Horizontal
        },
        reverseOrdering = true,
        alignment = Alignment.End,
    ) {
        SActionsScope.block()
    }
}

object SActionsScope {

    @Composable
    fun <E : CancelOrInProgress> Action(
        actionOrElseOrDisabled: ActionOrElse<Unit, E>?,
        titleOrIcon: TitleOrIcon,
        importance: Importance = Importance.default,
    ) {
        FButton(
            actionOrElseOrDisabled = actionOrElseOrDisabled,
            palette = importance.palette,
            titleOrIcon = titleOrIcon,
        )
    }
}