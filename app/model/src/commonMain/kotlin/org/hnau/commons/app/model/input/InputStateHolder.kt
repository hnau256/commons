package org.hnau.commons.app.model.input

import arrow.core.Option
import kotlinx.coroutines.flow.StateFlow
import org.hnau.commons.kotlin.KeyValue

interface InputStateHolder<S, E, I : InputType<S>> {

    val type: I

    val stateWithErrorOrNone: StateFlow<KeyValue<S, Option<E>>>

    val updateState: StateFlow<((S) -> Unit)?>
}