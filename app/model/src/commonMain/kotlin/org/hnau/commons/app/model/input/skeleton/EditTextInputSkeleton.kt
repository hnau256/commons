package org.hnau.commons.app.model.input.skeleton

import arrow.core.Either
import arrow.core.None
import arrow.core.some
import kotlinx.serialization.Serializable
import org.hnau.commons.app.model.input.InputModelPrototype
import org.hnau.commons.app.model.input.InputParser
import org.hnau.commons.app.model.input.InputType
import org.hnau.commons.kotlin.it

@Serializable
data class EditTextInputSkeleton(
    val input: InputSkeleton<String, String>,
) {

    companion object {

        fun createForExisting(
            initialValue: String,
        ): EditTextInputSkeleton = EditTextInputSkeleton(
            input = InputSkeleton.create(
                initialValue = initialValue.some(),
                initialState = initialValue,
            )
        )

        fun createForNew(
            defaultValue: String,
        ): EditTextInputSkeleton = EditTextInputSkeleton(
            input = InputSkeleton.create(
                initialValue = None,
                initialState = defaultValue,
            )
        )
    }

    inline fun <E, V> toModelPrototype(
        configParser: (parser: InputParser<String, Nothing, String>) -> InputParser<String, E, V>,
    ): InputModelPrototype<String, E, V, InputType.Edit> = InputModelPrototype(
        skeleton = input,
        type = InputType.Edit(
            contentType = InputType.Edit.ContentType.Text,
        ),
        parser = InputParser.createIdentity<String>().let(configParser)
    )

    fun toModelPrototype(): InputModelPrototype<String, Nothing, String, InputType.Edit> =
        toModelPrototype(::it)
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