package org.hnau.commons.app.model.input.factory

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import org.hnau.commons.app.model.input.InputModel
import org.hnau.commons.app.model.input.InputSkeleton
import org.hnau.commons.app.model.input.InputType
import org.hnau.commons.kotlin.coroutines.flow.state.mutable.toMutableStateFlowAsInitial

fun <S, V, E, I : InputType<S>> InputModelFactory<S, V, E, I>.createModel(
    scope: CoroutineScope,
    skeleton: InputSkeleton<S, V>,
    enabled: StateFlow<Boolean> = true.toMutableStateFlowAsInitial(),
): InputModel<S, V, E, I> = InputModel(
    scope = scope,
    enabled = enabled,
    skeleton = skeleton,
    type = type,
    parse = parsingMapper.parse,
)