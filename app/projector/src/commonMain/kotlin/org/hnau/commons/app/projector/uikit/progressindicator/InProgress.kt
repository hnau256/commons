package org.hnau.commons.app.projector.uikit.progressindicator

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import org.hnau.commons.app.projector.uikit.state.BooleanStateContent
import org.hnau.commons.app.projector.uikit.transition.TransitionSpec
import org.hnau.commons.kotlin.foldBoolean
import kotlinx.coroutines.flow.StateFlow

@Composable
fun InProgress(
    inProgress: StateFlow<Boolean>,
    fillMaxSize: Boolean = true,
) {
    inProgress
        .collectAsState()
        .value
        .BooleanStateContent(
            modifier = fillMaxSize.foldBoolean(
                ifTrue = { Modifier.fillMaxSize() },
                ifFalse = { Modifier },
            ),
            transitionSpec = TransitionSpec.crossfade(),
            label = "InProgress",
        ) {
            fillMaxSize.foldBoolean(
                ifTrue = { ProgressIndicatorInBox() },
                ifFalse = { CircularProgressIndicator() }
            )
        }
}