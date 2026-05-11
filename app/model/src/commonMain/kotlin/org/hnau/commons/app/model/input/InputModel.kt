@file:UseSerializers(
    MutableStateFlowSerializer::class,
)

package org.hnau.commons.app.model.input

import arrow.core.Either
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.UseSerializers
import org.hnau.commons.app.model.input.skeleton.InputSkeleton
import org.hnau.commons.app.model.utils.Editable
import org.hnau.commons.kotlin.KeyValue
import org.hnau.commons.kotlin.coroutines.flow.state.mapState
import org.hnau.commons.kotlin.serialization.MutableStateFlowSerializer

class InputModel<S, E, V, I : InputType<S, E, V>>(
    scope: CoroutineScope,
    private val skeleton: InputSkeleton<S>,
    private val type: I,
    val enabled: StateFlow<Boolean>,
) {

    val state: MutableStateFlow<S>
        get() = skeleton.state

    val stateWithValueOrError: StateFlow<KeyValue<S, Either<E, V>>> = skeleton
        .state
        .mapState(scope) { state ->
            val valueOrError = type
                .parser
                .parse(state)
            KeyValue(state, valueOrError)
        }

    val valueOrError: StateFlow<Either<E, V>> = stateWithValueOrError.mapState(
        scope = scope,
        transform = KeyValue<*, Either<E, V>>::value,
    )

    val editable: StateFlow<Editable<V>> = valueOrError.mapState(scope) { valueOrError ->
        valueOrError.fold(
            ifLeft = { Editable.Incorrect },
            ifRight = { value ->
                Editable.Value(
                    value = value,
                    changed = value != skeleton.initialValue,
                )
            }
        )
    }
}