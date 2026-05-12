package org.hnau.commons.app.model.input.skeleton

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import kotlinx.serialization.Serializable
import org.hnau.commons.app.model.input.InputModelPrototype
import org.hnau.commons.app.model.input.InputParser
import org.hnau.commons.app.model.input.InputType
import org.hnau.commons.kotlin.it
import org.hnau.commons.kotlin.toEither

@Serializable
data class EditDecimalInputSkeleton(
    val input: InputSkeleton<String>,
) {

    constructor(
        initial: BigDecimal,
    ) : this(
        input = InputSkeleton(
            initialValue = initial.toStringExpanded(),
        )
    )

    inline fun <E, V> toModelPrototype(
        configParser: (parser: InputParser<String, UnableParseStringToDecimalError, BigDecimal>) -> InputParser<String, E, V>,
    ): InputModelPrototype<String, E, V, InputType.Edit> = InputModelPrototype(
        skeleton = input,
        type = InputType.Edit(
            contentType = InputType.Edit.ContentType.Decimal,
        ),
        parser = InputParser<String, UnableParseStringToDecimalError, BigDecimal>(
            parse = { string ->
                Result
                    .runCatching { BigDecimal.parseString(string) }
                    .toEither()
                    .mapLeft { UnableParseStringToDecimalError }
            },
        ).let(configParser)
    )

    fun toModelPrototype(): InputModelPrototype<String, UnableParseStringToDecimalError, BigDecimal, InputType.Edit> =
        toModelPrototype(::it)
}


data object UnableParseStringToDecimalError