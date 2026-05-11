package org.hnau.commons.app.model.input.skeleton

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.Serializable
import org.hnau.commons.app.model.input.InputModel
import org.hnau.commons.app.model.input.InputType
import org.hnau.commons.kotlin.coroutines.flow.state.mutable.toMutableStateFlowAsInitial

@Serializable
data class FlagInputSkeleton(
    val input: InputSkeleton<Boolean>,
) {

    constructor(
        initial: Boolean,
    ) : this(
        input = InputSkeleton(
            initialValue = initial,
        )
    )

    fun toModel(
        scope: CoroutineScope,
        enabled: StateFlow<Boolean> = true.toMutableStateFlowAsInitial(),
    ): InputModel<Boolean, Nothing, Boolean, InputType.Flag> = InputModel(
        scope = scope,
        skeleton = input,
        type = InputType.Flag,
        enabled = enabled,
    )
}