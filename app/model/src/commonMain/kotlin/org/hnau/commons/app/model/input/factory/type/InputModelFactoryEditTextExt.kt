package org.hnau.commons.app.model.input.factory.type

import arrow.core.Either
import org.hnau.commons.app.model.input.InputParser
import org.hnau.commons.app.model.input.InputType
import org.hnau.commons.app.model.input.factory.InputModelFactory
import org.hnau.commons.kotlin.it

fun <E, V> InputModelFactory.Companion.editText(
    encoder: (V) -> String,
    configParser: (InputParser<String, Nothing, String>) -> InputParser<String, E, V>,
): InputModelFactory<String, E, V, InputType.Edit> = edit(
    contentType = InputType.Edit.ContentType.Text,
    encoder = encoder,
    parser = InputParser
        .createIdentity<String>()
        .let(configParser),
)

val InputModelFactory.Companion.editText: InputModelFactory<String, Nothing, String, InputType.Edit>
    get() = inputModelFactoryEditText


private val inputModelFactoryEditText: InputModelFactory<String, Nothing, String, InputType.Edit> =
    InputModelFactory.editText(
        encoder = ::it,
        configParser = ::it,
    )



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