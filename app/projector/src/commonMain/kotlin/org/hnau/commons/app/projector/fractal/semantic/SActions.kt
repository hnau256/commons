package org.hnau.commons.app.projector.fractal.semantic

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.hnau.commons.app.projector.fractal.FButton
import org.hnau.commons.app.projector.fractal.utils.Distance
import org.hnau.commons.app.projector.fractal.utils.local
import org.hnau.commons.app.projector.fractal.utils.size.FUnits
import org.hnau.commons.app.projector.utils.Orientation
import org.hnau.commons.app.projector.utils.TitleOrIcon
import org.hnau.commons.app.projector.utils.fold
import org.hnau.commons.kotlin.coroutines.ActionOrElse
import org.hnau.commons.kotlin.coroutines.CancelOrInProgress

@Composable
fun SActions(
    modifier: Modifier = Modifier,
    block: @Composable SActionsScope.() -> Unit,
) {
    val orientation = when (Distance.local.distance) {
        0 -> Orientation.Vertical
        else -> Orientation.Horizontal
    }

    val arrangement = FUnits
        .local
        .padding[orientation]
        .medium
        .let(Arrangement::spacedBy)
    orientation.fold(
        ifHorizontal = {
            Row(
                modifier = modifier,
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = arrangement,
            ) {
                val scope: SActionsScope = remember {
                    SActionsScopeImpl(
                        itemModifier = Modifier.fillMaxHeight(),
                    )
                }
                scope.block()
            }
        },
        ifVertical = {
            Column(
                modifier = modifier,
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = arrangement,
            ) {
                val scope: SActionsScope = remember {
                    SActionsScopeImpl(
                        itemModifier = Modifier.fillMaxWidth(),
                    )
                }
                scope.block()
            }
        },
    )

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