package org.hnau.commons.app.model.input.factory.type

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import org.hnau.commons.app.model.input.InputParser
import org.hnau.commons.app.model.input.InputType
import org.hnau.commons.app.model.input.factory.InputModelFactory
import org.hnau.commons.kotlin.it
import org.hnau.commons.kotlin.toEither


data object UnableParseStringToDecimalError

fun <E, V> InputModelFactory.Companion.editDecimal(
    encoder: (V) -> BigDecimal,
    configParser: (InputParser<String, UnableParseStringToDecimalError, BigDecimal>) -> InputParser<String, E, V>,
): InputModelFactory<String, E, V, InputType.Edit> = edit(
    contentType = InputType.Edit.ContentType.Decimal,
    encoder = { value -> encoder(value).toStringExpanded() },
    parser = InputParser<String, UnableParseStringToDecimalError, BigDecimal> { string ->
        Result
            .runCatching { BigDecimal.parseString(string) }
            .toEither()
            .mapLeft { UnableParseStringToDecimalError }
    }.let(configParser),
)

val InputModelFactory.Companion.editDecimal: InputModelFactory<String, UnableParseStringToDecimalError, BigDecimal, InputType.Edit>
    get() = inputModelFactoryEditDecimal


private val inputModelFactoryEditDecimal: InputModelFactory<String, UnableParseStringToDecimalError, BigDecimal, InputType.Edit> =
    InputModelFactory.editDecimal(
        encoder = ::it,
        configParser = ::it,
    )