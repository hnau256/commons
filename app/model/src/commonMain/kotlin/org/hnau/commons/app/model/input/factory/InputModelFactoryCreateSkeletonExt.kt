package org.hnau.commons.app.model.input.factory

import arrow.core.None
import arrow.core.some
import org.hnau.commons.app.model.input.InputSkeleton
import org.hnau.commons.app.model.input.InputType
import org.hnau.commons.kotlin.coroutines.flow.state.mutable.toMutableStateFlowAsInitial
import org.hnau.commons.kotlin.foldBoolean

fun <S, V, E, I : InputType<S>> InputModelFactory<S, V, E, I>.createSkeleton(
    value: V,
    useValueAsInitial: Boolean,
): InputSkeleton<S, V> = InputSkeleton(
    initialValue = useValueAsInitial.foldBoolean(
        ifTrue = { value.some() },
        ifFalse = { None },
    ),
    state = value
        .let(parsingMapper.encode)
        .toMutableStateFlowAsInitial(),
)