package org.hnau.commons.app.model.input.skeleton

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import kotlinx.serialization.Serializable
import org.hnau.commons.app.model.input.InputModel
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

    inline fun <E, V> toModelFactory(
        configParser: (mapper: InputParser<String, UnableParseStringToDecimalError, BigDecimal>) -> InputParser<String, E, V>,
    ): InputModel.Factory<String, E, V, InputType.Edit> = InputModel.Factory.simple(
        skeleton = input,
        type = InputType.Edit,
        parser = InputParser<String, UnableParseStringToDecimalError, BigDecimal>(
            parse = { string ->
                Result
                    .runCatching { BigDecimal.parseString(string) }
                    .toEither()
                    .mapLeft { UnableParseStringToDecimalError }
            },
        ).let(configParser)
    )

    fun toModelFactory(): InputModel.Factory<String, UnableParseStringToDecimalError, BigDecimal, InputType.Edit> =
        toModelFactory(::it)
}


data object UnableParseStringToDecimalError