package org.hnau.commons.app.model.input.factory.type

import arrow.core.NonEmptyList
import org.hnau.commons.app.model.input.InputParser
import org.hnau.commons.app.model.input.InputType
import org.hnau.commons.app.model.input.factory.InputModelFactory
import org.hnau.commons.kotlin.it


fun <S> InputModelFactory.Companion.createVariant(
    variants: NonEmptyList<S>,
): InputModelFactory<S, Nothing, S, InputType.Variant<S>> = InputModelFactory(
    type = InputType.Variant(variants),
    encoder = ::it,
    parser = InputParser.createIdentity(),
)
