package org.hnau.commons.app.projector.fractal.semantic.input

import androidx.compose.ui.text.input.ImeAction
import org.hnau.commons.app.projector.fractal.semantic.input.edit.SEditType

sealed interface SInputType<S, E, V> {

    val mapper: SInputMapper<S, E, V>

    data object Flag : SInputType<Boolean, Nothing, Boolean> {

        override val mapper: SInputMapper<Boolean, Nothing, Boolean> =
            SInputMapper.createIdentity()
    }

    data class Edit<E, V>(
        val type: SEditType<E, V>,
        val imeAction: ImeAction = ImeAction.Unspecified,
    ) : SInputType<String, E, V> {

        override val mapper: SInputMapper<String, E, V>
            get() = type.mapper
    }
}

