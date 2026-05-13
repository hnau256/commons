@file:UseSerializers(
    OptionSerializer::class,
)

package org.hnau.commons.app.model.input

import arrow.core.None
import arrow.core.Option
import arrow.core.serialization.OptionSerializer
import arrow.core.some
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import org.hnau.commons.kotlin.coroutines.flow.state.mutable.toMutableStateFlowAsInitial
import org.hnau.commons.kotlin.it

@Serializable
data class InputSkeleton<S, V>(
    val initialValue: Option<V>,
    val state: MutableStateFlow<S>,
) {

    data class Factory<S, V>(
        val toStateConverter: (V) -> S,
    ) {

        fun createForNew(
            defaultValue: V,
        ): InputSkeleton<S, V> = InputSkeleton(
            initialValue = None,
            state = defaultValue
                .let(toStateConverter)
                .toMutableStateFlowAsInitial()
        )

        fun createForEdit(
            value: V,
        ): InputSkeleton<S, V> = InputSkeleton(
            initialValue = value.some(),
            state = value
                .let(toStateConverter)
                .toMutableStateFlowAsInitial()
        )

        companion object {

            operator fun <V> invoke(): Factory<V, V> = Factory(
                toStateConverter = ::it,
            )
        }
    }

    fun <E, I : InputType<S>> toModelPrototype(
        type: I,
        parser: InputParser<S, E, V>,
    ): InputModelPrototype<S, E, V, I> = InputModelPrototype(
        skeleton = this,
        type = type,
        parser = parser,
    )

    fun <I : InputType<S>> toModelPrototype(
        type: I,
        parse: (S) -> V,
    ): InputModelPrototype<S, Nothing, V, I> = toModelPrototype(
        type = type,
        parser = InputParser.createAlwaysSuccess(parse),
    )
}

fun <S, I : InputType<S>> InputSkeleton<S, S>.toModelPrototype(
    type: I,
): InputModelPrototype<S, Nothing, S, I> = toModelPrototype(
    type = type,
    parse = ::it,
)