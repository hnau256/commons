package org.hnau.commons.app.projector.uikit

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.hnau.commons.app.projector.uikit.state.StateContent
import org.hnau.commons.app.projector.uikit.transition.TransitionSpec
import org.hnau.commons.app.projector.utils.Icon
import org.hnau.commons.app.projector.utils.PaddingValuesZero
import org.hnau.commons.kotlin.coroutines.ActionOrElse
import org.hnau.commons.kotlin.coroutines.CancelOrInProgress
import org.hnau.commons.kotlin.invoke

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
    actionOrElseOrDisabled: ActionOrElse<A, E>?,
    modifier: Modifier = Modifier,
    size: Dp,
    contentPadding: PaddingValues = PaddingValuesZero,
    transitionSpec: AnimatedContentTransitionScope<ActionOrElse<A, E>?>.() -> ContentTransform = TransitionSpec.rememberCenter(),
    actionIcon: (@Composable () -> Unit)?,
) {
    actionOrElseOrDisabled.StateContent(
        modifier = modifier,
        transitionSpec = transitionSpec,
        label = "ActionOrElseOrNullIcon",
        contentKey = { actionOrElse ->
            when (actionOrElse) {
                null, is ActionOrElse.Action -> 0
                is ActionOrElse.Else -> 1
            }
        }
    ) { actionOrElseOrDisabled ->
        when (actionOrElseOrDisabled) {
            null, is ActionOrElse.Action -> actionIcon?.let { actionIcon ->
                Box(
                    modifier = Modifier
                        .padding(contentPadding)
                        .size(size),
                    contentAlignment = Alignment.Center,
                ) {
                    actionIcon()
                }
            }

            is ActionOrElse.Else -> Box(
                modifier = Modifier.padding(contentPadding),
                contentAlignment = Alignment.Center,
            ) {
                when (actionOrElseOrDisabled.cancelOrInProgress) {

                    CancelOrInProgress.InProgress -> Unit

                    is CancelOrInProgress.Cancel -> Icon(
                        icon = Icons.Default.Close,
                        modifier = Modifier.size(size - 4.dp),
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
                CircularProgressIndicator(
                    modifier = Modifier.size(size),
                    strokeWidth = size / 12,
                )
            }
        }
    }
}