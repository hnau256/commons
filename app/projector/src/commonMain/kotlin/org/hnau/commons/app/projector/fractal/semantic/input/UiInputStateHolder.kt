package org.hnau.commons.app.projector.fractal.semantic.input

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import org.hnau.commons.app.model.input.InputStateHolder
import org.hnau.commons.app.model.input.InputType
import org.hnau.commons.kotlin.KeyValue
import org.hnau.commons.kotlin.coroutines.flow.state.mapState

data class UiInputStateHolder<S, I : InputType<S>>(
    val type: I,
    val enabled: StateFlow<Boolean>,
    val createStateWithErrorOrNull: (CoroutineScope) -> StateFlow<KeyValue<S, String?>>,
    val updateState: (newState: S) -> Unit,
)

inline fun <S, E, I : InputType<S>> InputStateHolder<S, E, I>.toUiInputStateHolder(
    crossinline parseError: (S, E) -> String,
): UiInputStateHolder<S, I> = UiInputStateHolder(
    type = type,
    updateState = ::updateState,
    enabled = enabled,
    createStateWithErrorOrNull = { scope ->
        stateWithErrorOrNone.mapState(scope) { stateWithErrorOrNone ->
            stateWithErrorOrNone.map { errorOrNone ->
                errorOrNone.map { error ->
                    val state = stateWithErrorOrNone.key
                    parseError(state, error)
                }.getOrNull()
            }
        }
    }
)

fun <S, I : InputType<S>> InputStateHolder<S, Nothing, I>.toUiInputStateHolder(): UiInputStateHolder<S, I> =
    toUiInputStateHolder { _, _ -> "" }