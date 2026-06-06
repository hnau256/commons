package org.hnau.commons.app.model.input.factory

import org.hnau.commons.app.model.input.InputType
import org.hnau.commons.app.model.input.parser.ParsingMapper

data class InputModelFactory<S, V, E, I : InputType<S>>(
    val type: I,
    val parsingMapper: ParsingMapper<S, V, E>,
) {

    companion object
}