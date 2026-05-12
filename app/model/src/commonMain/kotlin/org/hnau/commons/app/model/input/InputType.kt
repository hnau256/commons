package org.hnau.commons.app.model.input

sealed interface InputType<S> {

    data object Flag : InputType<Boolean>

    data class Edit(
        val contentType: ContentType,
    ) : InputType<String> {


        enum class ContentType { Text, Integer, Decimal }
    }
}

