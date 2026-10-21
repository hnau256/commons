package org.hnau.commons.app.model.input.parser

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import org.hnau.commons.kotlin.it
import kotlin.jvm.JvmName

inline fun <S, ERROR_THIS, ERROR_OTHER, E, M, V> ParsingMapper<S, M, ERROR_THIS>.with(
    other: ParsingMapper<M, V, ERROR_OTHER>,
    crossinline ifThisError: (ERROR_THIS) -> E,
    crossinline ifOtherError: (ERROR_OTHER) -> E,
): ParsingMapper<S, V, E> = ParsingMapper(
    encode = { value ->
        value
            .let(other.encode)
            .let(encode)
    },
    parse = { state ->
        parse(state).fold(
            ifLeft = { thisError -> ifThisError(thisError).left() },
            ifRight = {
                other.parse(it).fold(
                    ifLeft = { otherError -> ifOtherError(otherError).left() },
                    ifRight = { result -> result.right() }
                )
            }
        )
    },
)

@JvmName("alwaysSuccessPlus")
operator fun <S, M, V, E> ParsingMapper<S, M, Nothing>.plus(
    other: ParsingMapper<M, V, E>,
): ParsingMapper<S, V, E> = with(
    other = other,
    ifThisError = ::it,
    ifOtherError = ::it
)

@JvmName("plusAlwaysSuccess")
operator fun <S, M, V, E> ParsingMapper<S, M, E>.plus(
    other: ParsingMapper<M, V, Nothing>,
): ParsingMapper<S, V, E> = with(
    other = other,
    ifThisError = ::it,
    ifOtherError = ::it
)

operator fun <S, M, V, ERROR_THIS, ERROR_OTHER> ParsingMapper<S, M, ERROR_THIS>.plus(
    other: ParsingMapper<M, V, ERROR_OTHER>,
): ParsingMapper<S, V, Either<ERROR_THIS, ERROR_OTHER>> = with(
    other = other,
    ifThisError = { error -> error.left() },
    ifOtherError = { error -> error.right() }
)