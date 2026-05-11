package org.hnau.commons.app.model.input.skeleton

import kotlinx.serialization.Serializable
import org.hnau.commons.app.model.input.InputModel
import org.hnau.commons.app.model.input.InputParser
import org.hnau.commons.app.model.input.InputType

@Serializable
data class FlagInputSkeleton(
    val input: InputSkeleton<Boolean>,
) {

    constructor(
        initial: Boolean,
    ) : this(
        input = InputSkeleton(
            initialValue = initial,
        )
    )

    fun toModelFactory(): InputModel.Factory<Boolean, Nothing, Boolean, InputType.Flag> =
        InputModel.Factory.simple(
            skeleton = input,
            type = InputType.Flag,
            parser = InputParser.createIdentity(),
        )
}