package org.hnau.commons.app.model.input

sealed interface InputType<S> {

    data object Flag : InputType<Boolean>

    data object Edit : InputType<String>
}

