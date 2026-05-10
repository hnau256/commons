package org.hnau.commons.app.projector.fractal.semantic.input.edit

import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import org.hnau.commons.app.projector.fractal.semantic.input.SInputMapper

interface SEditType<E, T> {

    data class Config(
        val keyboardType: KeyboardType,
        val capitalization: KeyboardCapitalization,
    )

    val config: Config

    val mapper: SInputMapper<String, E, T>

    companion object
}