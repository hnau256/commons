package hnau.common.app.projector.uikit.state

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import hnau.common.app.projector.uikit.progressindicator.ProgressIndicatorInBox
import hnau.common.kotlin.Loadable
import hnau.common.kotlin.fold

@Composable
fun <T> Loadable<T>.LoadableContent(
    modifier: Modifier = Modifier,
    transitionSpec: AnimatedContentTransitionScope<Loadable<T>>.() -> ContentTransform,
    contentAlignment: Alignment = Alignment.Center,
    loadingContent: @Composable () -> Unit = { ProgressIndicatorInBox() },
    readyContent: @Composable (value: T) -> Unit,
) {
    StateContent(
        modifier = modifier,
        contentKey = { localValue ->
            localValue.fold(
                ifLoading = { false },
                ifReady = { true },
            )
        },
        label = "LoadingOrReady",
        transitionSpec = transitionSpec,
        contentAlignment = contentAlignment,
    ) { localValue ->
        localValue.fold(
            ifLoading = { loadingContent() },
            ifReady = { value -> readyContent(value) }
        )
    }
}