package org.hnau.commons.app.test.app.projector

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import kotlinx.coroutines.CoroutineScope
import org.hnau.commons.app.projector.fractal.context.LocalFContext
import org.hnau.commons.app.projector.fractal.context.color
import org.hnau.commons.app.projector.fractal.utils.plus
import org.hnau.commons.app.projector.uikit.backbutton.BackButtonHost
import org.hnau.commons.app.projector.uikit.state.LoadableContent
import org.hnau.commons.app.projector.uikit.transition.TransitionSpec
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
        BackButtonHost(
            contentPadding = contentPadding,
            goBackHandler = model.goBackHandler,
            backButtonBackgroundColor = LocalFContext.current
                .run { copy(distance = distance + 1) }
                .color,
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
        }
    }
}