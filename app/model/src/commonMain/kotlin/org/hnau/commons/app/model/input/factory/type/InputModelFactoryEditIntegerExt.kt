package org.hnau.commons.app.model.input.factory.type

import com.ionspin.kotlin.bignum.integer.BigInteger
import org.hnau.commons.app.model.input.InputParser
import org.hnau.commons.app.model.input.InputType
import org.hnau.commons.app.model.input.factory.InputModelFactory
import org.hnau.commons.kotlin.it
import org.hnau.commons.kotlin.toEither


data object UnableParseStringToIntegerError

fun <E, V> InputModelFactory.Companion.editInteger(
    encoder: (V) -> BigInteger,
    configParser: (InputParser<String, UnableParseStringToIntegerError, BigInteger>) -> InputParser<String, E, V>,
): InputModelFactory<String, E, V, InputType.Edit> = edit(
    contentType = InputType.Edit.ContentType.Integer,
    encoder = { value -> encoder(value).toString() },
    parser = InputParser<String, UnableParseStringToIntegerError, BigInteger> { string ->
        Result
            .runCatching { BigInteger.parseString(string) }
            .toEither()
            .mapLeft { UnableParseStringToIntegerError }
    }.let(configParser),
)

val InputModelFactory.Companion.editInteger: InputModelFactory<String, UnableParseStringToIntegerError, BigInteger, InputType.Edit>
    get() = inputModelFactoryEditInteger


private val inputModelFactoryEditInteger: InputModelFactory<String, UnableParseStringToIntegerError, BigInteger, InputType.Edit> =
    InputModelFactory.editInteger(
        encoder = ::it,
        configParser = ::it,
    )