package org.hnau.commons.app.projector.fractal.semantic.input

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import org.hnau.commons.kotlin.value

operator fun <SI, EI, EO, M, TO> SInputMapper<SI, EI, M>.plus(
    other: SInputMapper<M, EO, TO>,
): SInputMapper<SI, Either<EI, EO>, TO> = SInputMapper(
    direct = { value -> direct(other.direct(value)) },
    parse = { string ->
        parse(string).fold(
            ifLeft = { inError -> inError.left().left() },
            ifRight = {
                other.parse(it).fold(
                    ifLeft = { outError -> outError.right().left() },
                    ifRight = { result -> result.right() }
                )
            }
        )
    },
    convertErrorToString = { inOrOutError ->
        inOrOutError.fold(
            ifLeft = { inError -> convertErrorToString(inError) },
            ifRight = { outError -> other.convertErrorToString(outError) },
        )
    }
)

fun <S, E, V> SInputMapper<S, Either<Nothing, E>, V>.simplify(): SInputMapper<S, E, V> =
    SInputMapper(
        direct = direct,
        parse = { value -> parse(value).mapLeft { error -> error.value } },
        convertErrorToString = { error -> convertErrorToString(error.right()) },
    )