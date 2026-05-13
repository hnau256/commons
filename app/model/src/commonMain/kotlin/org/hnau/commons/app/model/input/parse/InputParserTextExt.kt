package org.hnau.commons.app.model.input.parse

import arrow.core.Either
import org.hnau.commons.app.model.input.InputParser

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