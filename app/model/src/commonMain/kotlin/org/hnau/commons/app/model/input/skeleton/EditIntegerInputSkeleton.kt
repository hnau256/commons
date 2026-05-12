package org.hnau.commons.app.model.input.skeleton

import com.ionspin.kotlin.bignum.integer.BigInteger
import kotlinx.serialization.Serializable
import org.hnau.commons.app.model.input.InputModelPrototype
import org.hnau.commons.app.model.input.InputParser
import org.hnau.commons.app.model.input.InputType
import org.hnau.commons.kotlin.it
import org.hnau.commons.kotlin.toEither

@Serializable
data class EditIntegerInputSkeleton(
    val input: InputSkeleton<String>,
) {

    constructor(
        initial: BigInteger,
    ) : this(
        input = InputSkeleton(
            initialValue = initial.toString(),
        )
    )

    inline fun <E, V> toModelPrototype(
        configParser: (parser: InputParser<String, UnableParseStringToIntegerError, BigInteger>) -> InputParser<String, E, V>,
    ): InputModelPrototype<String, E, V, InputType.Edit> = InputModelPrototype(
        skeleton = input,
        type = InputType.Edit(
            contentType = InputType.Edit.ContentType.Integer,
        ),
        parser = InputParser<String, UnableParseStringToIntegerError, BigInteger>(
            parse = { string ->
                Result
                    .runCatching { BigInteger.parseString(string) }
                    .toEither()
                    .mapLeft { UnableParseStringToIntegerError }
            },
        ).let(configParser)
    )

    fun toModelPrototype(): InputModelPrototype<String, UnableParseStringToIntegerError, BigInteger, InputType.Edit> =
        toModelPrototype(::it)
}


data object UnableParseStringToIntegerError