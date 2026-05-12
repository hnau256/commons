package org.hnau.commons.app.model.input.skeleton

import arrow.core.Either
import kotlinx.serialization.Serializable
import org.hnau.commons.app.model.input.InputModel
import org.hnau.commons.app.model.input.InputParser
import org.hnau.commons.app.model.input.InputType
import org.hnau.commons.kotlin.it

@Serializable
data class EditTextInputSkeleton(
    val input: InputSkeleton<String>,
) {

    constructor(
        initial: String,
    ) : this(
        input = InputSkeleton(
            initialValue = initial,
        )
    )

    inline fun <E, V> toModelFactory(
        configParser: (mapper: InputParser<String, Nothing, String>) -> InputParser<String, E, V>,
    ): InputModel.Factory<String, E, V, InputType.Edit> = InputModel.Factory.simple(
        skeleton = input,
        type = InputType.Edit(
            contentType = InputType.Edit.ContentType.Text,
        ),
        parser = InputParser.createIdentity<String>().let(configParser)
    )

    fun toModelFactory(): InputModel.Factory<String, Nothing, String, InputType.Edit> =
        toModelFactory(::it)
}

data class StringIsTooShort(
    val expectedMinLength: Int,
    val actualLength: Int,
)

fun InputParser.Companion.createStringMinLengthValidator(
    minLength: Int,
): InputParser<String, StringIsTooShort, String> = InputParser.createValidator { string ->
    val actualLength = string.length
    if (actualLength < minLength) {
        return@createValidator Either.Left(
            StringIsTooShort(
                expectedMinLength = minLength,
                actualLength = actualLength,
            )
        )
    }
    Either.Right(Unit)
}


data class StringIsTooLong(
    val expectedMaxLength: Int,
    val actualLength: Int,
)

fun InputParser.Companion.createStringMaxLengthValidator(
    maxLength: Int,
): InputParser<String, StringIsTooLong, String> = InputParser.createValidator { string ->
    val actualLength = string.length
    if (actualLength > maxLength) {
        return@createValidator Either.Left(
            StringIsTooLong(
                expectedMaxLength = maxLength,
                actualLength = actualLength,
            )
        )
    }
    Either.Right(Unit)
}