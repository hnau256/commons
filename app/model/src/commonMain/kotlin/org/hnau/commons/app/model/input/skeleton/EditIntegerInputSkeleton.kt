package org.hnau.commons.app.model.input.skeleton

import com.ionspin.kotlin.bignum.integer.BigInteger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.Serializable
import org.hnau.commons.app.model.input.InputModel
import org.hnau.commons.app.model.input.InputParser
import org.hnau.commons.app.model.input.InputType
import org.hnau.commons.kotlin.coroutines.flow.state.mutable.toMutableStateFlowAsInitial
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

    inline fun <E, V> toModel(
        scope: CoroutineScope,
        enabled: StateFlow<Boolean> = true.toMutableStateFlowAsInitial(),
        configParser: (mapper: InputParser<String, UnableParseStringToIntegerError, BigInteger>) -> InputParser<String, E, V>,
    ): InputModel<String, E, V, InputType.Edit<E, V>> =
        InputModel(
            scope = scope,
            skeleton = input,
            type = InputType.Edit(
                parser = InputParser<String, UnableParseStringToIntegerError, BigInteger>(
                    parse = { string ->
                        Result
                            .runCatching { BigInteger.parseString(string) }
                            .toEither()
                            .mapLeft { UnableParseStringToIntegerError }
                    },
                ).let(configParser),
            ),
            enabled = enabled,
        )

    fun toModel(
        scope: CoroutineScope,
        enabled: StateFlow<Boolean> = true.toMutableStateFlowAsInitial(),
    ): InputModel<String, UnableParseStringToIntegerError, BigInteger, InputType.Edit<UnableParseStringToIntegerError, BigInteger>> =
        toModel(
            scope = scope,
            enabled = enabled,
            configParser = ::it,
        )
}


data object UnableParseStringToIntegerError