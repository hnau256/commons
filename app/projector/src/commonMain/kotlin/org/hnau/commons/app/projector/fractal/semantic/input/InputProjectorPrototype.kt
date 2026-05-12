package org.hnau.commons.app.projector.fractal.semantic.input

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import org.hnau.commons.app.model.input.InputType
import org.hnau.commons.app.projector.utils.Drawable
import org.hnau.commons.kotlin.KeyValue
import org.hnau.commons.kotlin.coroutines.flow.state.mapState

data class InputProjectorPrototype<S, I : InputType<S>>(
    val stateHolder: UiInputStateHolder<S, I>,
    val createContentProjector: (type: I, state: StateFlow<S>, updateState: (S) -> Unit) -> InputContentProjector,
) {

    fun createInputProjector(
        scope: CoroutineScope,
        title: String,
        icon: Drawable?
    ): InputProjector {
        val stateWithErrorOrNull = stateHolder.createStateWithErrorOrNull(scope)
        return InputProjector(
            title = title,
            icon = icon,
            errorMessage = stateWithErrorOrNull.mapState(
                scope = scope,
                transform = KeyValue<*, String?>::value
            ),
            contentProjector = createContentProjector(
                stateHolder.type,
                stateWithErrorOrNull.mapState(
                    scope = scope,
                    transform = KeyValue<S, *>::key,
                ),
                stateHolder.updateState,
            )
        )
    }
}

fun <S, I : InputType<S>> UiInputStateHolder<S, I>.toInputProjectorPrototype(
    createContentProjector: (type: I, state: StateFlow<S>, updateState: (S) -> Unit) -> InputContentProjector,
): InputProjectorPrototype<S, I> = InputProjectorPrototype(
    stateHolder = this,
    createContentProjector = createContentProjector,
)