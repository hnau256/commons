package org.hnau.commons.app.model.input.parse

import com.ionspin.kotlin.bignum.integer.BigInteger
import org.hnau.commons.app.model.input.InputParser
import org.hnau.commons.kotlin.toEither


private val stringToBigIntegerParser: InputParser<String, UnableParseStringToIntegerError, BigInteger> =
    InputParser { string ->
        Result
            .runCatching { BigInteger.parseString(string) }
            .toEither()
            .mapLeft { UnableParseStringToIntegerError }
    }

val InputParser.Companion.stringToBigInteger: InputParser<String, UnableParseStringToIntegerError, BigInteger>
    get() = stringToBigIntegerParser

data object UnableParseStringToIntegerError