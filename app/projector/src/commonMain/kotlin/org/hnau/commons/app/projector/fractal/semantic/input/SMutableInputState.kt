package org.hnau.commons.app.projector.fractal.semantic.input

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.hnau.commons.kotlin.coroutines.flow.state.Stickable
import org.hnau.commons.kotlin.coroutines.flow.state.mutable.toMutableStateFlowAsInitial
import org.hnau.commons.kotlin.coroutines.flow.state.stick
import org.hnau.commons.kotlin.ifTrue

internal data class SMutableInputState<S, E, V>(
    val type: SInputType<S, E, V>,
    val state: StateFlow<S>,
    val update: (S) -> Unit,
)

internal fun <S, E, V> MutableStateFlow<SInputState<S, E, V>>.toMutableState(
    scope: CoroutineScope,
): StateFlow<SMutableInputState<S, E, V>> = stick(
    scope = scope,
) { _, initialState ->
    object : Stickable<SInputState<S, E, V>, SMutableInputState<S, E, V>> {

        private val state: MutableStateFlow<S> =
            initialState.state.toMutableStateFlowAsInitial()

        override fun tryUpdateValue(
            newValue: SInputState<S, E, V>,
        ): Boolean = (newValue.type == initialState.type).apply {
            ifTrue { state.value = newValue.state }
        }

        override val result: SMutableInputState<S, E, V>
            get() = SMutableInputState(
                type = initialState.type,
                state = state,
                update = { newState ->
                    value = initialState.copy(
                        state = newState
                    )
                }
            )
    }
}