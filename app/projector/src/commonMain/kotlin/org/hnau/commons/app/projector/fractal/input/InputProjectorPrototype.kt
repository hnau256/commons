package org.hnau.commons.app.projector.fractal.input

import androidx.compose.runtime.Composable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import org.hnau.commons.app.model.input.InputStateHolder
import org.hnau.commons.app.model.input.InputType
import org.hnau.commons.app.projector.fractal.utils.Importance
import org.hnau.commons.kotlin.KeyValue
import org.hnau.commons.kotlin.coroutines.flow.state.mapState

data class InputProjectorPrototype<S, E, I : InputType<S>>(
    val stateHolder: InputStateHolder<S, E, I>,
    val createContentProjector: (
        type: I,
        state: StateFlow<S>,
        updateState: StateFlow<((S) -> Unit)?>,
    ) -> InputContentProjector,
) {

    inline fun createInputProjector(
        scope: CoroutineScope,
        title: String,
        noinline startAccessory: (@Composable () -> Unit)? = null,
        importanceToActivate: Importance? = Importance.default,
        titleMaxLines: Int = 1,
        crossinline displayError: (S, E) -> String,
    ): InputProjector = InputProjector(
        titleMaxLines = titleMaxLines,
        importanceToActivate = importanceToActivate,
        title = title,
        startAccessory = startAccessory,
        errorMessage = stateHolder
            .stateWithErrorOrNone
            .mapState(scope) { stateWithErrorOrNone ->
                stateWithErrorOrNone
                    .value
                    .map { error ->
                        val state = stateWithErrorOrNone.key
                        displayError(state, error)
                    }
                    .getOrNull()
            },
        contentProjector = createContentProjector(
            stateHolder.type,
            stateHolder
                .stateWithErrorOrNone
                .mapState(
                    scope = scope,
                    transform = KeyValue<S, *>::key,
                ),
            stateHolder.updateState,
        )
    )
}

fun <S, I : InputType<S>> InputProjectorPrototype<S, Nothing, I>.createInputProjector(
    scope: CoroutineScope,
    title: String,
    startAccessory: (@Composable () -> Unit)? = null,
    titleMaxLines: Int = 1,
    importanceToActivate: Importance? = Importance.default,
): InputProjector = createInputProjector(
    scope = scope,
    title = title,
    startAccessory = startAccessory,
    titleMaxLines = titleMaxLines,
    importanceToActivate = importanceToActivate,
    displayError = { _, _ -> "" }
)

internal fun <S, E, I : InputType<S>> InputStateHolder<S, E, I>.toInputProjectorPrototype(
    createContentProjector: (
        type: I,
        state: StateFlow<S>,
        updateState: StateFlow<((S) -> Unit)?>,
    ) -> InputContentProjector,
): InputProjectorPrototype<S, E, I> = InputProjectorPrototype(
    stateHolder = this,
    createContentProjector = createContentProjector,
)