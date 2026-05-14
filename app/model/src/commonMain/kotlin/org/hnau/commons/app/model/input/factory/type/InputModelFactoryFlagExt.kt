package org.hnau.commons.app.model.input.factory.type

import org.hnau.commons.app.model.input.InputParser
import org.hnau.commons.app.model.input.InputType
import org.hnau.commons.app.model.input.factory.InputModelFactory
import org.hnau.commons.kotlin.it


val InputModelFactory.Companion.flag: InputModelFactory<Boolean, Nothing, Boolean, InputType.Flag>
    get() = flagInputModelFactory


private val flagInputModelFactory: InputModelFactory<Boolean, Nothing, Boolean, InputType.Flag> =
    InputModelFactory(
        type = InputType.Flag,
        encoder = ::it,
        parser = InputParser.createIdentity(),
    )