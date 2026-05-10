package org.hnau.commons.app.projector.fractal.semantic.input.edit.type

import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import com.ionspin.kotlin.bignum.integer.BigInteger
import org.hnau.commons.app.projector.fractal.semantic.input.SInputMapper
import org.hnau.commons.app.projector.fractal.semantic.input.edit.SEditType
import org.hnau.commons.kotlin.toEither

fun SEditType.Companion.integer(
    unableParseStringToIntegerErrorMessage: String,
): SEditType<Throwable, BigInteger> = object : SEditType<Throwable, BigInteger> {

    override val config: SEditType.Config = SEditType.Config(
        keyboardType = KeyboardType.Number,
        capitalization = KeyboardCapitalization.None,
    )

    override val mapper: SInputMapper<String, Throwable, BigInteger> =
        SInputMapper(
            direct = BigInteger::toString,
            parse = { string ->
                Result
                    .runCatching { BigInteger.parseString(string) }
                    .toEither()
            },
            convertErrorToString = { unableParseStringToIntegerErrorMessage }
        )
}