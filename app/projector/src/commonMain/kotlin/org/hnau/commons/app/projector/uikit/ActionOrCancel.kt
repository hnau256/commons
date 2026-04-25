package org.hnau.commons.app.projector.uikit

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import org.hnau.commons.app.projector.uikit.ActionOrCancel.Type
import org.hnau.commons.kotlin.coroutines.ActionOrElse
import org.hnau.commons.kotlin.coroutines.CancelOrInProgress
import org.hnau.commons.kotlin.invoke

data class ActionOrCancel(
    val onClick: () -> Unit,
    val type: Type,
) {
    enum class Type { Action, Cancel }

}

val <E : CancelOrInProgress> ActionOrElse<Unit, E>.actionOrCancel: ActionOrCancel?
    get() = when (this) {
        is ActionOrElse.Action -> ActionOrCancel(
            onClick = { action() },
            type = Type.Action,
        )

        is ActionOrElse.Else -> when (val cancelOrInProgress = cancelOrInProgress) {
            CancelOrInProgress.InProgress -> null
            is CancelOrInProgress.Cancel -> ActionOrCancel(
                onClick = cancelOrInProgress.cancel,
                type = Type.Cancel,
            )
        }
    }

val <E : CancelOrInProgress> ActionOrElse<Unit, E>.rememberActionOrCancel: ActionOrCancel?
    @Composable
    get() = remember(this) { actionOrCancel }