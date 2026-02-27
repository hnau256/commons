package org.hnau.commons.app.projector.uikit.state

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun <T> T?.NullableStateContent(
    modifier: Modifier = Modifier,
    label: String = "ValueOrNull",
    nullContent: @Composable () -> Unit = {},
    contentAlignment: Alignment = Alignment.Center,
    transitionSpec: AnimatedContentTransitionScope<T?>.() -> ContentTransform,
    anyContent: @Composable (value: T & Any) -> Unit,
) {
    StateContent(
        modifier = modifier,
        label = label,
        contentKey = { localValue -> localValue != null },
        transitionSpec = transitionSpec,
        contentAlignment = contentAlignment,
    ) { localValue ->
        when (localValue) {
            null -> nullContent()
            else -> anyContent(localValue)
        }
    }
}