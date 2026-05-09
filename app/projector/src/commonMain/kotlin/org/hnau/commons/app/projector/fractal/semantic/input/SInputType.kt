package org.hnau.commons.app.projector.fractal.semantic.input

import androidx.compose.ui.text.input.ImeAction
import org.hnau.commons.app.projector.fractal.semantic.input.edit.SEditType

sealed interface SInputType<S, E, T> {

    val mapper: SInputMapper<S, E, T>

    data object Flag : SInputType<Boolean, Nothing, Boolean> {

        override val mapper: SInputMapper<Boolean, Nothing, Boolean> =
            SInputMapper.createIdentity()
    }

    data class Edit<E, T>(
        val type: SEditType<E, T>,
        val imeAction: ImeAction = ImeAction.Unspecified,
    ) : SInputType<String, E, T> {

        override val mapper: SInputMapper<String, E, T>
            get() = type.mapper
    }
}

