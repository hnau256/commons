package org.hnau.commons.app.model.input.parser

import arrow.core.Either
import arrow.core.right
import org.hnau.commons.kotlin.it
import org.hnau.commons.kotlin.mapper.Mapper


fun <V, E> ParsingMapper.Companion.createValidator(
    validate: (V) -> Either<E, Unit>,
): ParsingMapper<V, V, E> = ParsingMapper(
    encode = ::it,
    parse = { value -> validate(value).map { value } },
)

fun <S, V> Mapper<S, V>.toParsingMapper(): ParsingMapper<S, V, Nothing> = ParsingMapper(
    encode = reverse,
    parse = { state ->
        state
            .let(direct)
            .right()
    }
)