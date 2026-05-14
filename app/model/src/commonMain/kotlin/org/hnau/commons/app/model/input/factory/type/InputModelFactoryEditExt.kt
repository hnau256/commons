package org.hnau.commons.app.model.input.factory.type

import org.hnau.commons.app.model.input.InputParser
import org.hnau.commons.app.model.input.InputType
import org.hnau.commons.app.model.input.factory.InputModelFactory

fun <E, V> InputModelFactory.Companion.edit(
    contentType: InputType.Edit.ContentType,
    encoder: (V) -> String,
    parser: InputParser<String, E, V>,
): InputModelFactory<String, E, V, InputType.Edit> = InputModelFactory(
    type = InputType.Edit(contentType),
    encoder = encoder,
    parser = parser,
)