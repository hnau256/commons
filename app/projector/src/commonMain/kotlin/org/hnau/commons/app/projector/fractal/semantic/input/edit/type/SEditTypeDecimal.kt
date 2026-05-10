package org.hnau.commons.app.projector.fractal.semantic.input.edit.type

import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import org.hnau.commons.app.projector.fractal.semantic.input.SInputMapper
import org.hnau.commons.app.projector.fractal.semantic.input.edit.SEditType
import org.hnau.commons.kotlin.toEither

fun SEditType.Companion.decimal(
    unableParseStringToDecimalErrorMessage: String,
): SEditType<Throwable, BigDecimal> = object : SEditType<Throwable, BigDecimal> {

    override val config: SEditType.Config = SEditType.Config(
        keyboardType = KeyboardType.Decimal,
        capitalization = KeyboardCapitalization.None,
    )

    override val mapper: SInputMapper<String, Throwable, BigDecimal> =
        SInputMapper(
            direct = BigDecimal::toStringExpanded,
            parse = { string ->
                Result
                    .runCatching { BigDecimal.parseString(string) }
                    .toEither()
            },
            convertErrorToString = { unableParseStringToDecimalErrorMessage }
        )
}