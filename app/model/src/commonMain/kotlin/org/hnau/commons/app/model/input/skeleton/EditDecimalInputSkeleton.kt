package org.hnau.commons.app.model.input.skeleton

import com.ionspin.kotlin.bignum.decimal.BigDecimal
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

    inline fun <E, V> toModel(
        scope: CoroutineScope,
        enabled: StateFlow<Boolean> = true.toMutableStateFlowAsInitial(),
        configParser: (mapper: InputParser<String, UnableParseStringToDecimalError, BigDecimal>) -> InputParser<String, E, V>,
    ): InputModel<String, E, V, InputType.Edit<E, V>> = InputModel(
        scope = scope,
        skeleton = input,
        type = InputType.Edit(
            parser = InputParser<String, UnableParseStringToDecimalError, BigDecimal>(
                parse = { string ->
                    Result
                        .runCatching { BigDecimal.parseString(string) }
                        .toEither()
                        .mapLeft { UnableParseStringToDecimalError }
                },
            ).let(configParser),
        ),
        enabled = enabled,
    )

    fun toModel(
        scope: CoroutineScope,
        enabled: StateFlow<Boolean> = true.toMutableStateFlowAsInitial(),
    ): InputModel<String, UnableParseStringToDecimalError, BigDecimal, InputType.Edit<UnableParseStringToDecimalError, BigDecimal>> =
        toModel(
            scope = scope,
            enabled = enabled,
            configParser = ::it,
        )
}


data object UnableParseStringToDecimalError