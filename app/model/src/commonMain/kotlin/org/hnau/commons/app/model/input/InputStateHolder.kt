package org.hnau.commons.app.model.input

import arrow.core.Option
import kotlinx.coroutines.flow.StateFlow
import org.hnau.commons.gen.fold.annotations.Fold
import org.hnau.commons.kotlin.KeyValue
import org.hnau.commons.kotlin.coroutines.flow.state.mutable.toMutableStateFlowAsInitial

interface InputStateHolder<S, E, I : InputType<S>> {

    val type: I

    val stateWithErrorOrNone: StateFlow<KeyValue<S, Option<E>>>

    val updateState: StateFlow<((S) -> Unit)?>

    val decoration: StateFlow<Decoration?>
        get() = noDecorationStateFlow

    @Fold
    enum class Decoration { InProgress, Selected }

    companion object {

        private val noDecorationStateFlow: StateFlow<Decoration?> =
            null.toMutableStateFlowAsInitial()
    }
}