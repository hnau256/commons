package org.hnau.commons.app.model.input

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import org.hnau.commons.app.model.input.InputSkeleton
import org.hnau.commons.kotlin.coroutines.flow.state.mutable.toMutableStateFlowAsInitial

data class InputModelPrototype<S, E, V, I : InputType<S>>(
    val skeleton: InputSkeleton<S, V>,
    val type: I,
    val parser: InputParser<S, E, V>,
) {

    fun toInputModel(
        scope: CoroutineScope,
        enabled: StateFlow<Boolean> = true.toMutableStateFlowAsInitial(),
    ): InputModel<S, E, V, I> = InputModel(
        scope = scope,
        enabled = enabled,
        skeleton = skeleton,
        type = type,
        parser = parser,
    )
}