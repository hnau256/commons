package org.hnau.commons.app.model.input

import arrow.core.NonEmptyList

sealed interface InputType<S> {

    data object Flag : InputType<Boolean>

    data class Edit(
        val contentType: ContentType,
    ) : InputType<String> {


        enum class ContentType { Text, Integer, Decimal }
    }

    data class Variant<S>(
        val variants: NonEmptyList<S>,
    ) : InputType<S>
}

