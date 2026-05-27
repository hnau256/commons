@file:UseSerializers(
    MutableStateFlowSerializer::class,
)

package org.hnau.commons.app.model.input

import arrow.core.Either
import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.UseSerializers
import org.hnau.commons.app.model.utils.Editable
import org.hnau.commons.kotlin.KeyValue
import org.hnau.commons.kotlin.coroutines.flow.state.mapState
import org.hnau.commons.kotlin.serialization.MutableStateFlowSerializer

class InputModel<S, V, E, I : InputType<S>>(
    scope: CoroutineScope,
    private val skeleton: InputSkeleton<S, V>,
    override val type: I,
    private val parse: (S) -> Either<E, V>,
    override val enabled: StateFlow<Boolean>,
) : InputStateHolder<S, E, I> {

    private val stateWithValueOrError: StateFlow<KeyValue<S, Either<E, V>>> = skeleton
        .state
        .mapState(scope) { state ->
            val valueOrError = parse(state)
            KeyValue(state, valueOrError)
        }

    override val stateWithErrorOrNone: StateFlow<KeyValue<S, Option<E>>> =
        stateWithValueOrError.mapState(scope) { stateWithValueOrError ->
            stateWithValueOrError.map { valueOrError ->
                valueOrError.fold(
                    ifLeft = { error -> Some(error) },
                    ifRight = { None },
                )
            }
        }

    override fun updateState(newState: S) {
        skeleton.state.value = newState
    }

    val valueOrError: StateFlow<Either<E, V>> = stateWithValueOrError.mapState(
        scope = scope,
        transform = KeyValue<*, Either<E, V>>::value,
    )

    val editable: StateFlow<Editable<V>> = Editable.create(
        scope = scope,
        valueOrNone = valueOrError.mapState(
            scope = scope,
            transform = Either<*, V>::getOrNone,
        ),
        initialValueOrNone = skeleton.initialValue,
    )
}