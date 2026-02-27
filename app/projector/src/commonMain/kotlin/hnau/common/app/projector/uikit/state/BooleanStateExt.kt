package hnau.common.app.projector.uikit.state

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import arrow.core.identity

@Composable
fun Boolean.BooleanStateContent(
    modifier: Modifier = Modifier,
    transitionSpec: AnimatedContentTransitionScope<Boolean>.() -> ContentTransform,
    label: String = "Boolean",
    contentAlignment: Alignment = Alignment.Center,
    falseContent: @Composable () -> Unit = {},
    trueContent: @Composable () -> Unit,
) {
    StateContent(
        modifier = modifier,
        label = label,
        contentKey = ::identity,
        transitionSpec = transitionSpec,
        contentAlignment = contentAlignment,
    ) { localValue ->
        when (localValue) {
            false -> falseContent()
            true -> trueContent()
        }
    }
}