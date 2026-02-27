package org.hnau.commons.app.projector.uikit

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.hnau.commons.app.projector.uikit.state.StateContent
import org.hnau.commons.app.projector.uikit.state.TransitionSpec
import org.hnau.commons.kotlin.coroutines.ActionOrCancel
import org.hnau.commons.kotlin.foldNullable

@Composable
fun ActionOrCancel(
    actionOrCancel: ActionOrCancel,
    icon: @Composable () -> Unit,
    content: @Composable (
        iconOrCancel: @Composable () -> Unit,
        onClick: () -> Unit,
    ) -> Unit,
) {
    val (actionOrCancelOrInProgress, onClick) =
        remember(actionOrCancel) {
            when (actionOrCancel) {
                is ActionOrCancel.Action -> ActionOrCancelOrInProgress.Action to actionOrCancel.action
                is ActionOrCancel.Cancel -> ActionOrCancelOrInProgress.Cancel to actionOrCancel.cancel
            }
        }
    ActionOrCancelOrInProgress(
        actionOrCancelOrInProgress = actionOrCancelOrInProgress,
        icon = icon,
    ) { iconOrCancelOrInProgress ->
        content(
            iconOrCancelOrInProgress,
            onClick,
        )
    }
}

@Composable
fun ActionOrNull(
    actionOrNull: (() -> Unit)?,
    icon: @Composable () -> Unit,
    content: @Composable (
        iconOrInProgress: @Composable () -> Unit,
        onClick: (() -> Unit)?,
    ) -> Unit,
) {
    val actionOrCancelOrInProgress =
        remember(actionOrNull) {
            actionOrNull.foldNullable(
                ifNull = { ActionOrCancelOrInProgress.Cancel },
                ifNotNull = { ActionOrCancelOrInProgress.Action },
            )
        }
    ActionOrCancelOrInProgress(
        actionOrCancelOrInProgress = actionOrCancelOrInProgress,
        icon = icon,
    ) { iconOrCancelOrInProgress ->
        content(
            iconOrCancelOrInProgress,
            actionOrNull,
        )
    }
}

private enum class ActionOrCancelOrInProgress { Action, Cancel, InProgress }

@Composable
private fun ActionOrCancelOrInProgress(
    actionOrCancelOrInProgress: ActionOrCancelOrInProgress,
    icon: @Composable () -> Unit,
    content: @Composable (
        iconOrCancelOrInProgress: @Composable () -> Unit,
    ) -> Unit,
) {
    content {
        actionOrCancelOrInProgress.StateContent(
            label = "ActionOrCancelOrNone",
            transitionSpec = TransitionSpec.crossfade(),
            contentKey = { actionOrCancel ->
                when (actionOrCancel) {
                    ActionOrCancelOrInProgress.Action -> 0
                    ActionOrCancelOrInProgress.Cancel -> 1
                    ActionOrCancelOrInProgress.InProgress -> 2
                }
            },
        ) { actionOrCancel ->
            when (actionOrCancel) {
                ActionOrCancelOrInProgress.Action -> icon()

                ActionOrCancelOrInProgress.Cancel -> {
                    Box(
                        contentAlignment = Alignment.Center,
                    ) {
//                        Icon(
//                            icon = Icons.Default.Close,
//                            modifier = Modifier.size(20.dp),
//                            tint = MaterialTheme.colorScheme.primary,
//                        )
                        ProgressIndicator()
                    }
                }

                ActionOrCancelOrInProgress.InProgress ->
                    ProgressIndicator()
            }
        }
    }
}

@Composable
private fun ProgressIndicator() {
    CircularProgressIndicator(
        modifier = Modifier.size(24.dp),
        strokeWidth = 2.dp,
    )
}
