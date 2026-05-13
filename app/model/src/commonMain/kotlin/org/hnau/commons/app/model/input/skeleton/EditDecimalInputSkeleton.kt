@file:UseSerializers(
    BigDecimalSerializer::class
)

package org.hnau.commons.app.model.input.skeleton

import arrow.core.None
import arrow.core.some
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import org.hnau.commons.app.model.input.InputModelPrototype
import org.hnau.commons.app.model.input.InputParser
import org.hnau.commons.app.model.input.InputType
import org.hnau.commons.kotlin.it
import org.hnau.commons.kotlin.serialization.BigDecimalSerializer
import org.hnau.commons.kotlin.toEither

@Serializable
data class EditDecimalInputSkeleton(
    val input: InputSkeleton<String, BigDecimal>,
) {

    companion object {

        fun createForExisting(
            initialValue: BigDecimal,
        ): EditDecimalInputSkeleton = EditDecimalInputSkeleton(
            input = InputSkeleton.create(
                initialValue = initialValue.some(),
                initialState = initialValue.toStringExpanded(),
            )
        )

        fun createForNew(
            defaultValue: BigDecimal,
        ): EditDecimalInputSkeleton = EditDecimalInputSkeleton(
            input = InputSkeleton.create(
                initialValue = None,
                initialState = defaultValue.toStringExpanded(),
            )
        )
    }

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