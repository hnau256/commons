package hnau.common.app.projector.uikit

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import hnau.common.app.projector.utils.Overcompose
import hnau.common.app.projector.utils.copy
import hnau.common.app.projector.utils.map
import hnau.common.app.projector.utils.plus

@Composable
fun FullScreen(
    modifier: Modifier = Modifier,
    backButtonWidth: Dp,
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
    val insets = WindowInsets.systemBars.asPaddingValues()
    Overcompose(
        modifier = modifier.fillMaxSize(),
        top = {
            top(
                insets.map(
                    start = { start -> start + backButtonWidth },
                    bottom = { 0.dp },
                )
            )
        },
        bottom = {
            bottom(
                insets.copy(
                    top = 0.dp,
                )
            )
        }
    ) { contentPadding ->
        content(
            contentPadding + insets.copy(
                top = 0.dp,
                bottom = 0.dp,
            ),
        )
    }
}