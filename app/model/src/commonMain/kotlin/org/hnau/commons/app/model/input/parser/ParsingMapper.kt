package org.hnau.commons.app.model.input.parser

import arrow.core.Either

data class ParsingMapper<S, V, E>(
    val encode: (V) -> S,
    val parse: (S) -> Either<E, V>,
) {

    companion object
}