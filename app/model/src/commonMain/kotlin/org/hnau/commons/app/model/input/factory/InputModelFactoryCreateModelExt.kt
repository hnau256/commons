package org.hnau.commons.app.model.input.factory

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import org.hnau.commons.app.model.input.InputModel
import org.hnau.commons.app.model.input.InputSkeleton
import org.hnau.commons.app.model.input.InputType
import org.hnau.commons.kotlin.coroutines.flow.state.mutable.toMutableStateFlowAsInitial

fun <S, E, V, I : InputType<S>> InputModelFactory<S, E, V, I>.createModel(
    scope: CoroutineScope,
    skeleton: InputSkeleton<S, V>,
    enabled: StateFlow<Boolean> = true.toMutableStateFlowAsInitial(),
): InputModel<S, E, V, I> = InputModel(
    scope = scope,
    enabled = enabled,
    skeleton = skeleton,
    type = type,
    parser = parser,
)