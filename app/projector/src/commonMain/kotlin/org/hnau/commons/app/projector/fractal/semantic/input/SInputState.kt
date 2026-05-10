package org.hnau.commons.app.projector.fractal.semantic.input

import arrow.core.Either
import org.hnau.commons.app.model.utils.Editable

data class SInputState<S, E, V>(
    val initialValue: V,
    val type: SInputType<S, E, V>,
    val state: S,
) {

    val valueOrError: Either<E, V>
        get() = type.mapper.parse(state)

    val editable: Editable<V>
        get() = valueOrError.fold(
            ifLeft = { Editable.Incorrect },
            ifRight = { value ->
                Editable.Value(
                    value = value,
                    changed = value != initialValue,
                )
            }
        )

    companion object {

        fun <S, E, V> create(
            type: SInputType<S, E, V>,
            initialValue: V,
        ): SInputState<S, E, V> = SInputState(
            initialValue = initialValue,
            type = type,
            state = type.mapper.direct(initialValue),
        )
    }
}