package org.hnau.commons.app.test.app.projector

import androidx.annotation.Dimension
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import arrow.core.nonEmptyListOf
import kotlinx.coroutines.CoroutineScope
import org.hnau.commons.app.projector.fractal.SSteps
import org.hnau.commons.app.projector.fractal.SText
import org.hnau.commons.app.projector.fractal.context.LocalFContext
import org.hnau.commons.app.projector.fractal.context.color
import org.hnau.commons.app.projector.fractal.context.containerOverlay
import org.hnau.commons.app.projector.fractal.distance.LocalDistance
import org.hnau.commons.app.projector.fractal.size.units
import org.hnau.commons.app.projector.uikit.backbutton.BackButtonHost
import org.hnau.commons.app.projector.uikit.state.LoadableContent
import org.hnau.commons.app.projector.uikit.transition.TransitionSpec
import org.hnau.commons.app.projector.utils.Orientation
import org.hnau.commons.app.test.app.model.RootModel
import org.hnau.commons.gen.pipe.annotations.Pipe
import org.hnau.commons.kotlin.coroutines.flow.state.mapWithScope
import org.hnau.commons.kotlin.map

class RootProjector(
    scope: CoroutineScope,
    dependencies: Dependencies,
    private val model: RootModel,
) {

    @Pipe
    interface Dependencies {

        fun stack(): RootStackProjector.Dependencies

        companion object
    }

    private val stack = model
        .stack
        .mapWithScope(scope) { scope, stackModelOrLoading ->
            stackModelOrLoading.map { stackModel ->
                RootStackProjector(
                    scope = scope,
                    model = stackModel,
                    dependencies = dependencies.stack(),
                )
            }
        }

    @Composable
    fun Content(
        contentPadding: PaddingValues,
    ) {
        var position by remember { mutableFloatStateOf(0f) }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(LocalDistance.current.units.paddingValues.vertical.medium),
            contentAlignment = Alignment.Center,
        ) {
            SSteps(
                modifier = Modifier.fillMaxWidth(),
                orientation = Orientation.Horizontal,
                weights = nonEmptyListOf(1f, 0.5f),
                position = position,
                onPositionChanged = { position = it },
            ) { i ->
                SText(
                    text = "Item$i"
                )
            }
        }
        /*BackButtonHost(
            contentPadding = contentPadding,
            goBackHandler = model.goBackHandler,
            backButtonBackgroundColor = LocalFContext.current.containerOverlay().color,
        ) { contentPadding ->
            stack
                .collectAsState()
                .value
                .LoadableContent(
                    transitionSpec = TransitionSpec.rememberCrossfade(),
                ) { stack ->
                    stack.Content(
                        contentPadding = contentPadding
                    )
                }
        }*/
    }
}