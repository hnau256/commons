package org.hnau.commons.app.model.input

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import org.hnau.commons.kotlin.value

operator fun <SI, EI, EO, M, TO> InputParser<SI, EI, M>.plus(
    other: InputParser<M, EO, TO>,
): InputParser<SI, Either<EI, EO>, TO> = InputParser(
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
)

fun <S, E, V> InputParser<S, Either<Nothing, E>, V>.simplify(): InputParser<S, E, V> =
    InputParser(
        parse = { value -> parse(value).mapLeft { error -> error.value } },
    )