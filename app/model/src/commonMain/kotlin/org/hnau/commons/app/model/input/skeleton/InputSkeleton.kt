@file:UseSerializers(
    MutableStateFlowSerializer::class,
    OptionSerializer::class,
)

package org.hnau.commons.app.model.input.skeleton

import arrow.core.Option
import arrow.core.none
import arrow.core.serialization.OptionSerializer
import arrow.core.some
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import org.hnau.commons.kotlin.coroutines.flow.state.mutable.toMutableStateFlowAsInitial
import org.hnau.commons.kotlin.serialization.MutableStateFlowSerializer

@Serializable
data class InputSkeleton<S, V>(
    val initialValue: Option<V>,
    val state: MutableStateFlow<S>,
) {

    companion object {

        fun <S, V> create(
            initialValue: Option<V>,
            initialState: S,
        ): InputSkeleton<S, V> = InputSkeleton(
            initialValue = initialValue,
            state = initialState.toMutableStateFlowAsInitial(),
        )
    }
}

