package org.hnau.commons.app.model.input

import arrow.core.Either
import arrow.core.right
import org.hnau.commons.kotlin.it

data class InputParser<S, E, V>(
    val parse: (S) -> Either<E, V>,
) {

    companion object {

        fun <S, V> createAlwaysSuccess(
            convert: (S) -> V,
        ): InputParser<S, Nothing, V> = InputParser(
            parse = { state -> convert(state).right() },
        )

        fun <T> createIdentity(): InputParser<T, Nothing, T> =
            createAlwaysSuccess(::it)

        fun <V, E> createValidator(
            validate: (V) -> Either<E, Unit>,
        ): InputParser<V, E, V> = InputParser(
            parse = { value -> validate(value).map { value } },
        )
    }
}