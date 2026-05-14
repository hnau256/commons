package org.hnau.commons.app.model.input.factory

import org.hnau.commons.app.model.input.InputParser
import org.hnau.commons.app.model.input.InputType

data class InputModelFactory<S, E, V, I : InputType<S>>(
    val type: I,
    val encoder: (V) -> S,
    val parser: InputParser<S, E, V>,
) {

    companion object
}