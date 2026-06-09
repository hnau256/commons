package org.hnau.commons.app.projector.fractal

import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.hnau.commons.app.projector.fractal.distance.LocalDistance
import org.hnau.commons.app.projector.fractal.size.units
import org.hnau.commons.app.projector.fractal.table.STable
import org.hnau.commons.app.projector.fractal.table.STableScope
import org.hnau.commons.app.projector.fractal.table.Subtable
import org.hnau.commons.app.projector.fractal.utils.Importance
import org.hnau.commons.app.projector.uikit.line.LineScope
import org.hnau.commons.app.projector.uikit.line.weight
import org.hnau.commons.app.projector.utils.Orientation
import org.hnau.commons.app.projector.utils.TitleOrIcon
import org.hnau.commons.app.projector.utils.fold
import org.hnau.commons.kotlin.coroutines.ActionOrElse
import org.hnau.commons.kotlin.coroutines.CancelOrInProgress
import androidx.compose.runtime.remember as rememberInCompose

@Composable
fun SActions(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val distance = LocalDistance.current
    val orientation = when (distance.distance) {
        0 -> Orientation.Vertical
        else -> Orientation.Horizontal
    }
    SLine(
        modifier = modifier,
        orientation = orientation,
        reverseOrdering = true,
        separation = distance.units.padding.along.small,
    ) {
        content()
        orientation.fold(
            ifHorizontal = { Spacer(Modifier.weight(1f)) },
            ifVertical = {},
        )
    }
}

@Composable
fun SActions(
    orientation: Orientation,
    actions: @Composable STableActionsScope.() -> Unit,
) {
    STable(
        orientation = orientation,
        reverseOrdering = true,
    ) {
        val scope = STableActionsScope.remember(this)
        scope.actions()
    }
}

@Composable
fun STableScope.SActions(
    actions: @Composable STableActionsScope.() -> Unit,
) {
    Subtable(
        reverseOrdering = true,
    ) {
        val scope = STableActionsScope.remember(this)
        scope.actions()
    }
}

data class STableActionsScope(
    private val tableScope: STableScope,
) : LineScope by tableScope {

    companion object {

        @Composable
        fun remember(
            tableScope: STableScope,
        ): STableActionsScope = rememberInCompose(tableScope) {
            STableActionsScope(
                tableScope = tableScope,
            )
        }
    }

    @Composable
    fun <E : CancelOrInProgress> Action(
        actionOrElseOrDisabled: ActionOrElse<Unit, E>?,
        titleOrIcon: TitleOrIcon,
        modifier: Modifier = Modifier,
        importanceToActivate: Importance? = Importance.default,
    ) {
        tableScope.SCell(
            modifier = modifier,
        ) {
            SButton(
                actionOrElseOrDisabled = actionOrElseOrDisabled,
                titleOrIcon = titleOrIcon,
                importanceToActivate = importanceToActivate,
            )
        }
    }
}