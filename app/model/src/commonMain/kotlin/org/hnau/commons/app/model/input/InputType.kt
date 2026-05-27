package org.hnau.commons.app.model.input

import arrow.core.NonEmptyList

sealed interface InputType<S> {

    data object Flag : InputType<Boolean>

    data object Edit : InputType<String>

    data class Variant<S>(
        val variants: NonEmptyList<S>,
    ) : InputType<S>
}

