package org.hnau.commons.app.projector.fractal.semantic.input

import arrow.core.Either
import arrow.core.right
import org.hnau.commons.kotlin.it
import org.hnau.commons.kotlin.mapper.Mapper
import org.hnau.commons.kotlin.mapper.equality

data class SInputMapper<S, E, V>(
    val direct: (V) -> S,
    val parse: (S) -> Either<E, V>,
    val convertErrorToString: (E) -> String,
) {

    companion object {

        fun <S, V> createAlwaysSuccess(
            mapper: Mapper<V, S>,
        ): SInputMapper<S, Nothing, V> = SInputMapper(
            direct = mapper.direct,
            parse = { state -> mapper.reverse(state).right() },
            convertErrorToString = { "" },
        )

        fun <T> createIdentity(): SInputMapper<T, Nothing, T> =
            createAlwaysSuccess(Mapper.equality())

        fun <V, E> createValidator(
            validate: (V) -> Either<E, Unit>,
            convertErrorToString: (E) -> String,
        ): SInputMapper<V, E, V> = SInputMapper(
            direct = ::it,
            parse = { value -> validate(value).map { value } },
            convertErrorToString = convertErrorToString,
        )
    }
}