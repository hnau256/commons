package hnau.common.app.projector.uikit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import hnau.common.app.projector.uikit.utils.Dimens
import hnau.common.app.projector.utils.AnimatedNullableVisibility
import hnau.common.app.projector.utils.AnimatedVisibilityTransitions
import hnau.common.app.projector.utils.horizontalDisplayPadding

@Composable
fun ErrorPanel(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    button: @Composable (() -> Unit)? = null,
) = Column(
    modifier = modifier
        .horizontalDisplayPadding()
        .fillMaxSize(),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(
        Dimens.separation,
        Alignment.CenterVertically,
    ),
) {
    CompositionLocalProvider(
        LocalTextStyle provides MaterialTheme.typography.titleMedium,
    ) {
        title()
    }
    AnimatedNullableVisibility(
        value = button,
        transitions = AnimatedVisibilityTransitions.vertical,
    ) { buttonLocal ->
        buttonLocal()
    }
}
