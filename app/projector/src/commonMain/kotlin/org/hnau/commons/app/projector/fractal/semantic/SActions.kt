package org.hnau.commons.app.projector.fractal.semantic

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import org.hnau.commons.app.projector.fractal.FButton
import org.hnau.commons.app.projector.fractal.utils.Distance
import org.hnau.commons.app.projector.fractal.utils.local
import org.hnau.commons.app.projector.utils.Orientation
import org.hnau.commons.app.projector.utils.TitleOrIcon
import org.hnau.commons.app.projector.utils.fold
import org.hnau.commons.app.projector.utils.rememberLet
import org.hnau.commons.kotlin.coroutines.ActionOrElse
import org.hnau.commons.kotlin.coroutines.CancelOrInProgress

@Composable
fun SActions(
    block: @Composable SActionsScope.() -> Unit,
) {
    val orientation = when (Distance.local.distance) {
        0 -> Orientation.Vertical
        else -> Orientation.Horizontal
    }
    val scope: SActionsScope = remember(orientation) {
        SActionsScopeImpl(
            itemModifier = orientation.fold(
                ifHorizontal = { Modifier. }
            )
        )
    }
}

interface SActionsScope {

    @Composable
    fun <E : CancelOrInProgress> Action(
        actionOrElseOrDisabled: ActionOrElse<Unit, E>?,
        titleOrIcon: TitleOrIcon,
        importance: Importance = Importance.default,
    )
}

private class SActionsScopeImpl(
    private val itemModifier: Modifier,
) : SActionsScope {

    @Composable
    override fun <E : CancelOrInProgress> Action(
        actionOrElseOrDisabled: ActionOrElse<Unit, E>?,
        titleOrIcon: TitleOrIcon,
        importance: Importance
    ) {
        FButton(
            actionOrElseOrDisabled = actionOrElseOrDisabled,
            palette = importance.palette,
            titleOrIcon = titleOrIcon,
            modifier = itemModifier,
        )
    }
}