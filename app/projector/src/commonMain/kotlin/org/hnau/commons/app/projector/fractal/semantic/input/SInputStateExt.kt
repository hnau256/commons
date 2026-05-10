package org.hnau.commons.app.projector.fractal.semantic.input

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.hnau.commons.kotlin.KeyValue
import org.hnau.commons.kotlin.coroutines.flow.state.Stickable
import org.hnau.commons.kotlin.coroutines.flow.state.mutable.onSet
import org.hnau.commons.kotlin.coroutines.flow.state.mutable.toMutableStateFlowAsInitial
import org.hnau.commons.kotlin.coroutines.flow.state.stick
import org.hnau.commons.kotlin.ifTrue


internal fun <S, E, V> MutableStateFlow<SInputState<S, E, V>>.toMutableState(
    scope: CoroutineScope,
): StateFlow<KeyValue<SInputType<S, E, V>, MutableStateFlow<S>>> = stick(
    scope = scope,
) { _, initialState ->
    object : Stickable<SInputState<S, E, V>, KeyValue<SInputType<S, E, V>, MutableStateFlow<S>>> {

        private val state: MutableStateFlow<S> =
            initialState.state.toMutableStateFlowAsInitial()

        override fun tryUpdateValue(
            newValue: SInputState<S, E, V>,
        ): Boolean = (newValue.type == initialState.type).apply {
            ifTrue { state.value = newValue.state }
        }

        override val result: KeyValue<SInputType<S, E, V>, MutableStateFlow<S>>
            get() = KeyValue(
                key = initialState.type,
                value = state.onSet { newState ->
                    value = initialState.copy(
                        state = newState
                    )
                }
            )
    }
}