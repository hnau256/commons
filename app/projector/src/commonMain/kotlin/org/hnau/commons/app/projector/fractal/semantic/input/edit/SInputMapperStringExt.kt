package org.hnau.commons.app.projector.fractal.semantic.input.edit

import arrow.core.Either
import org.hnau.commons.app.projector.fractal.semantic.input.SInputMapper

data class StringIsTooShortError(
    val minLength: Int,
    val actualLength: Int,
)

fun SInputMapper.Companion.createMinLengthValidator(
    minLength: Int,
    convertErrorToString: (StringIsTooShortError) -> String,
): SInputMapper<String, StringIsTooShortError, String> = SInputMapper.createValidator(
    validate = { source ->
        val actualLength = source.length
        if (actualLength >= minLength) {
            return@createValidator Either.Right(Unit)
        }
        Either.Left(
            StringIsTooShortError(
                minLength = minLength,
                actualLength = actualLength,
            )
        )
    },
    convertErrorToString = convertErrorToString,
)

data class StringIsTooLongError(
    val maxLength: Int,
    val actualLength: Int,
)

fun SInputMapper.Companion.createMaxLengthValidator(
    maxLength: Int,
    convertErrorToString: (StringIsTooLongError) -> String,
): SInputMapper<String, StringIsTooLongError, String> = SInputMapper.createValidator(
    validate = { source ->
        val actualLength = source.length
        if (actualLength >= maxLength) {
            return@createValidator Either.Right(Unit)
        }
        Either.Left(
            StringIsTooLongError(
                maxLength = maxLength,
                actualLength = actualLength,
            )
        )
    },
    convertErrorToString = convertErrorToString,
)