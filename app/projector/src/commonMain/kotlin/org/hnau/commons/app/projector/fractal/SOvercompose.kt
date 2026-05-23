package org.hnau.commons.app.projector.fractal

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.hnau.commons.app.projector.utils.Overcompose

@Composable
fun SOvercompose(
    modifier: Modifier = Modifier,
    top: @Composable () -> Unit = {},
    bottom: @Composable () -> Unit = {},
    content: @Composable () -> Unit,
) {
    Overcompose(
        modifier = modifier,
        contentPadding = PaddingValues.Zero,
        top = { _ -> top() },
        bottom = { _ -> bottom() },
        content = { contentPadding ->
            Box(modifier = Modifier.padding(contentPadding), propagateMinConstraints = true) {
                content()
            }
        },
    )
}
