package org.hnau.commons.app.model.input.factory

import org.hnau.commons.app.model.input.InputType
import org.hnau.commons.app.model.input.parser.ParsingMapper
import org.hnau.commons.app.model.input.parser.toParsingMapper
import org.hnau.commons.kotlin.mapper.Mapper
import org.hnau.commons.kotlin.mapper.equality

fun <S, V, E, I: InputType<S>> I.toInputModelFactory(
    parsingMapper: ParsingMapper<S, V, E>,
): InputModelFactory<S, V, E, I> = InputModelFactory(
    type = this,
    parsingMapper = parsingMapper,
)

fun <S, I: InputType<S>> I.toInputModelFactory(): InputModelFactory<S, S, Nothing, I> =
    toInputModelFactory(
        parsingMapper = Mapper
            .equality<S>()
            .toParsingMapper(),
    )