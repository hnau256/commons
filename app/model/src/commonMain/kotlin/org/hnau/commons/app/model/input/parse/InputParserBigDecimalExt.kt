package org.hnau.commons.app.model.input.parse

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import org.hnau.commons.app.model.input.InputParser
import org.hnau.commons.kotlin.toEither


private val stringToBigDecimalParser: InputParser<String, UnableParseStringToDecimalError, BigDecimal> =
    InputParser { string ->
        Result
            .runCatching { BigDecimal.parseString(string) }
            .toEither()
            .mapLeft { UnableParseStringToDecimalError }
    }

val InputParser.Companion.stringToBigDecimal: InputParser<String, UnableParseStringToDecimalError, BigDecimal>
    get() = stringToBigDecimalParser

data object UnableParseStringToDecimalError