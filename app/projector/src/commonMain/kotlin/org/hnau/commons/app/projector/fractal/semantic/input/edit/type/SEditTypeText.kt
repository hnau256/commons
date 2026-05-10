package org.hnau.commons.app.projector.fractal.semantic.input.edit.type

import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import org.hnau.commons.app.projector.fractal.semantic.input.SInputMapper
import org.hnau.commons.app.projector.fractal.semantic.input.edit.SEditType

fun SEditType.Companion.text(
    config: SEditType.Config = SEditType.Config(
        keyboardType = KeyboardType.Text,
        capitalization = KeyboardCapitalization.Sentences
    )
): SEditType<Nothing, String> = object : SEditType<Nothing, String> {

    override val config: SEditType.Config
        get() = config

    override val mapper: SInputMapper<String, Nothing, String> =
        SInputMapper.createIdentity()
}