package org.hnau.commons.app.projector.uikit

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.hnau.commons.app.projector.uikit.state.StateContent
import org.hnau.commons.app.projector.uikit.transition.TransitionSpec
import org.hnau.commons.app.projector.uikit.utils.Dimens
import org.hnau.commons.app.projector.utils.Icon
import org.hnau.commons.app.projector.utils.PaddingValuesZero
import org.hnau.commons.app.projector.utils.orNoAction
import org.hnau.commons.kotlin.coroutines.ActionOrElse
import org.hnau.commons.kotlin.coroutines.CancelOrInProgress
import org.hnau.commons.kotlin.invoke

@Composable
fun <E : CancelOrInProgress> ActionOrElseIconButton(
    modifier: Modifier = Modifier,
    actionOrElseOrNull: ActionOrElse<Unit, E>?,
    icon: @Composable () -> Unit,
) {
    val onClick = actionOrElseOrNull?.onClick
    IconButton(
        modifier = modifier,
        onClick = onClick.orNoAction,
        enabled = onClick != null,
    ) {
        ActionOrElseIcon(
            actionOrElseOrNull = actionOrElseOrNull,
            transitionSpec = TransitionSpec.both(),
            actionIcon = icon,
        )
    }
}

@Composable
fun <E : CancelOrInProgress> ActionOrElseButton(
    modifier: Modifier = Modifier,
    actionOrElseOrNull: ActionOrElse<Unit, E>?,
    icon: (@Composable () -> Unit)?,
    title: @Composable () -> Unit,
) {
    val onClick = actionOrElseOrNull?.onClick
    Button(
        modifier = modifier,
        onClick = onClick.orNoAction,
        enabled = onClick != null,
    ) {
        ActionOrElseIcon(
            actionOrElseOrNull = actionOrElseOrNull,
            transitionSpec = TransitionSpec.horizontal(),
            actionIcon = icon,
            contentPadding = PaddingValues(
                end = Dimens.smallSeparation,
            )
        )
        title()
    }
}

val <E : CancelOrInProgress> ActionOrElse<Unit, E>.onClick: (() -> Unit)?
    get() = when (this) {
        is ActionOrElse.Action -> {
            { action() }
        }

        is ActionOrElse.Else -> when (val cancelOrInProgress = cancelOrInProgress) {
            is CancelOrInProgress.Cancel -> cancelOrInProgress.cancel
            CancelOrInProgress.InProgress -> null
        }
    }

@Composable
fun <A, E : CancelOrInProgress> ActionOrElseIcon(
    actionOrElseOrNull: ActionOrElse<A, E>?,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValuesZero,
    transitionSpec: AnimatedContentTransitionScope<ActionOrElse<A, E>?>.() -> ContentTransform = TransitionSpec.both(),
    actionIcon: (@Composable () -> Unit)?,
) {
    actionOrElseOrNull.StateContent(
        modifier = modifier,
        transitionSpec = transitionSpec,
        label = "ActionOrElseOrNullIcon",
        contentKey = { actionOrElse ->
            when (actionOrElse) {
                null, is ActionOrElse.Action -> 0
                is ActionOrElse.Else -> 1
            }
        }
    ) { actionOrElseOrNull ->
        when (actionOrElseOrNull) {
            null, is ActionOrElse.Action -> actionIcon?.let { actionIcon ->
                Box(
                    modifier = Modifier.padding(contentPadding),
                    contentAlignment = Alignment.Center,
                ) {
                    actionIcon()
                }
            }

            is ActionOrElse.Else -> Box(
                modifier = Modifier.padding(contentPadding),
                contentAlignment = Alignment.Center,
            ) {
                when (actionOrElseOrNull.cancelOrInProgress) {

                    CancelOrInProgress.InProgress -> Unit

                    is CancelOrInProgress.Cancel -> Icon(
                        icon = Icons.Default.Close,
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp,
                )
            }
        }
    }
}