package org.hnau.commons.app.model.input.skeleton

import arrow.core.Either
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.Serializable
import org.hnau.commons.app.model.input.InputModel
import org.hnau.commons.app.model.input.InputParser
import org.hnau.commons.app.model.input.InputType
import org.hnau.commons.kotlin.coroutines.flow.state.mutable.toMutableStateFlowAsInitial
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

    inline fun <E, V> toModel(
        scope: CoroutineScope,
        enabled: StateFlow<Boolean> = true.toMutableStateFlowAsInitial(),
        configParser: (mapper: InputParser<String, Nothing, String>) -> InputParser<String, E, V>,
    ): InputModel<String, E, V, InputType.Edit<E, V>> =
        InputModel(
            scope = scope,
            skeleton = input,
            type = InputType.Edit(
                parser = InputParser.createIdentity<String>()
                    .let(configParser),
            ),
            enabled = enabled,
        )

    fun toModel(
        scope: CoroutineScope,
        enabled: StateFlow<Boolean> = true.toMutableStateFlowAsInitial(),
    ): InputModel<String, Nothing, String, InputType.Edit<Nothing, String>> = toModel(
        scope = scope,
        enabled = enabled,
        configParser = ::it,
    )
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
    if (actualLength < maxLength) {
        return@createValidator Either.Left(
            StringIsTooLong(
                expectedMaxLength = maxLength,
                actualLength = actualLength,
            )
        )
    }
    Either.Right(Unit)
}