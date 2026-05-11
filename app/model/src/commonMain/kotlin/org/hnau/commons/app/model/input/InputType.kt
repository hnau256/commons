package org.hnau.commons.app.model.input

sealed interface InputType<S, E, V> {

    val parser: InputParser<S, E, V>

    data object Flag : InputType<Boolean, Nothing, Boolean> {

        override val parser: InputParser<Boolean, Nothing, Boolean> =
            InputParser.createIdentity()
    }

    data class Edit<E, V>(
        override val parser: InputParser<String, E, V>,
    ) : InputType<String, E, V> {

        companion object
    }
}

