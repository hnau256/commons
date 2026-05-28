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
import org.hnau.commons.app.projector.uikit.table.Subtable
import org.hnau.commons.app.projector.uikit.table.TableScope
import org.hnau.commons.app.projector.utils.Orientation
import org.hnau.commons.app.projector.utils.TitleOrIcon
import org.hnau.commons.app.projector.utils.fold
import org.hnau.commons.kotlin.coroutines.ActionOrElse
import org.hnau.commons.kotlin.coroutines.CancelOrInProgress
import androidx.compose.runtime.remember as rememberInCompose

@Composable
fun SActions(
    modifier: Modifier = Modifier,
    block: @Composable SActionsScope.() -> Unit,
) {
    val fContext = LocalFContext.current
    val orientation = when (fContext.distance.distance) {
        0 -> Orientation.Vertical
        else -> Orientation.Horizontal
    }
    Line(
        modifier = modifier,
        orientation = orientation,
        reverseOrdering = true,
        separation = fContext.distance.units.padding.along.small,
    ) {
        SActionsScopeImpl.block()
        orientation.fold(
            ifHorizontal = { Spacer(Modifier.weight(1f)) },
            ifVertical = {},
        )
    }
}

interface SActionsScope {

    @Composable
    fun <E : CancelOrInProgress> Action(
        actionOrElseOrDisabled: ActionOrElse<Unit, E>?,
        titleOrIcon: TitleOrIcon,
        mood: Mood = Mood.Primary,
    )
}

private data object SActionsScopeImpl : SActionsScope {

    @Composable
    override fun <E : CancelOrInProgress> Action(
        actionOrElseOrDisabled: ActionOrElse<Unit, E>?,
        titleOrIcon: TitleOrIcon,
        mood: Mood
    ) {
        UpdateFContext(
            mood = mood,
        ) {
            SButton(
                actionOrElseOrDisabled = actionOrElseOrDisabled,
                titleOrIcon = titleOrIcon,
            )
        }
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
fun TableScope.SActions(
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
    private val tableScope: TableScope,
) {

    companion object {

        @Composable
        fun remember(
            tableScope: TableScope,
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
        mood: Mood = Mood.Primary,
    ) {
        UpdateFContext(
            mood = mood,
        ) {
            tableScope.SCell(
                modifier = modifier,
            ) { shape ->
                SButton(
                    shape = shape,
                    actionOrElseOrDisabled = actionOrElseOrDisabled,
                    titleOrIcon = titleOrIcon,
                )
            }
        }
    }
}