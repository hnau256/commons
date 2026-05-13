package org.hnau.commons.app.model.input.skeleton

import arrow.core.None
import arrow.core.some
import kotlinx.serialization.Serializable
import org.hnau.commons.app.model.input.InputModelPrototype
import org.hnau.commons.app.model.input.InputParser
import org.hnau.commons.app.model.input.InputType

@Serializable
data class FlagInputSkeleton(
    val input: InputSkeleton<Boolean, Boolean>,
) {

    companion object {

        fun createForExisting(
            initialValue: Boolean,
        ): FlagInputSkeleton = FlagInputSkeleton(
            input = InputSkeleton.create(
                initialValue = initialValue.some(),
                initialState = initialValue,
            )
        )

        fun createForNew(
            defaultValue: Boolean,
        ): FlagInputSkeleton = FlagInputSkeleton(
            input = InputSkeleton.create(
                initialValue = None,
                initialState = defaultValue,
            )
        )
    }

    fun toModelPrototype(): InputModelPrototype<Boolean, Nothing, Boolean, InputType.Flag> =
        InputModelPrototype(
            skeleton = input,
            type = InputType.Flag,
            parser = InputParser.createIdentity(),
        )
}