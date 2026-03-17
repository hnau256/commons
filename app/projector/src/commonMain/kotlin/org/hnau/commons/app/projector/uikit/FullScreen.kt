package org.hnau.commons.app.projector.uikit

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.hnau.commons.app.projector.utils.Overcompose
import org.hnau.commons.app.projector.utils.copy
import org.hnau.commons.app.projector.utils.map
import org.hnau.commons.app.projector.utils.plus

@Composable
fun FullScreen(
    contentPadding: PaddingValues,
    backButtonWidth: Dp,
    modifier: Modifier = Modifier,
    top: @Composable (contentPadding: PaddingValues) -> Unit = { contentPadding ->
        Box(
            modifier = Modifier
                .padding(paddingValues = contentPadding),
        )
    },
    bottom: @Composable (contentPadding: PaddingValues) -> Unit = { contentPadding ->
        Box(
            modifier = Modifier
                .padding(paddingValues = contentPadding),
        )
    },
    content: @Composable (contentPadding: PaddingValues) -> Unit,
) {
    Overcompose(
        modifier = modifier.fillMaxSize(),
        top = {
            top(
                contentPadding.map(
                    start = { start -> start + backButtonWidth },
                    bottom = { 0.dp },
                )
            )
        },
        bottom = {
            bottom(
                contentPadding.copy(
                    top = 0.dp,
                )
            )
        }
    ) { localContentPadding ->
        content(
            localContentPadding + contentPadding.copy(
                top = 0.dp,
                bottom = 0.dp,
            ),
        )
    }
}