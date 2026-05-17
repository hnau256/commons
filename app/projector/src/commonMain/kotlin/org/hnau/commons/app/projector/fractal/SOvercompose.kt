package org.hnau.commons.app.projector.fractal

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import org.hnau.commons.app.projector.fractal.utils.LocalSContentPadding
import org.hnau.commons.app.projector.utils.Overcompose

@Composable
fun SOvercompose(
    modifier: Modifier = Modifier,
    top: @Composable () -> Unit = {
        Box(
            modifier = Modifier.padding(LocalSContentPadding.current),
        )
    },
    bottom: @Composable () -> Unit = {
        Box(
            modifier = Modifier.padding(LocalSContentPadding.current),
        )
    },
    content: @Composable () -> Unit,
) {
    Overcompose(
        modifier = modifier,
        contentPadding = LocalSContentPadding.current,
        top = { contentPadding ->
            CompositionLocalProvider(
                LocalSContentPadding provides contentPadding
            ) {
                top()
            }
        },
        bottom = { contentPadding ->
            CompositionLocalProvider(
                LocalSContentPadding provides contentPadding
            ) {
                bottom()
            }
        },
        content = { contentPadding ->
            CompositionLocalProvider(
                LocalSContentPadding provides contentPadding
            ) {
                content()
            }
        }
    )
}