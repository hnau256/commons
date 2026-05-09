package org.hnau.commons.app.projector.fractal.semantic.input

import arrow.core.Either
import arrow.core.right
import org.hnau.commons.kotlin.it
import org.hnau.commons.kotlin.mapper.Mapper
import org.hnau.commons.kotlin.mapper.equality

data class SInputMapper<S, E, T>(
    val direct: (T) -> S,
    val parse: (S) -> Either<E, T>,
    val convertErrorToString: (E) -> String,
) {

    companion object {

        fun <S, T> createAlwaysSuccess(
            mapper: Mapper<T, S>,
        ): SInputMapper<S, Nothing, T> = SInputMapper(
            direct = mapper.direct,
            parse = { state -> mapper.reverse(state).right() },
            convertErrorToString = { "" },
        )

        fun <T> createIdentity(): SInputMapper<T, Nothing, T> =
            createAlwaysSuccess(Mapper.equality())

        fun <T, E> createValidator(
            validate: (T) -> Either<E, Unit>,
            convertErrorToString: (E) -> String,
        ): SInputMapper<T, E, T> = SInputMapper(
            direct = ::it,
            parse = { value -> validate(value).map { value } },
            convertErrorToString = convertErrorToString,
        )
    }
}