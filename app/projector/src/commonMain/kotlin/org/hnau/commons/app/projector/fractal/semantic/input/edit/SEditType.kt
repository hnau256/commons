package org.hnau.commons.app.projector.fractal.semantic.input.edit

import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.integer.BigInteger
import org.hnau.commons.app.projector.fractal.semantic.input.SInputMapper
import org.hnau.commons.kotlin.toEither

interface SEditType<E, T> {

    data class Config(
        val keyboardType: KeyboardType,
        val capitalization: KeyboardCapitalization,
    )

    val config: Config

    val mapper: SInputMapper<String, E, T>

    data class Text(
        override val config: Config = Config(
            keyboardType = KeyboardType.Text,
            capitalization = KeyboardCapitalization.Sentences
        ),
    ) : SEditType<Nothing, String> {

        override val mapper: SInputMapper<String, Nothing, String> =
            SInputMapper.createIdentity()
    }

    data class Decimal(
        private val unableParseStringToDecimalErrorMessage: String,
    ) : SEditType<Throwable, BigDecimal> {

        override val config: Config = Config(
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

    data class Integer(
        private val unableParseStringToIntegerErrorMessage: String,
    ) : SEditType<Throwable, BigInteger> {

        override val config: Config = Config(
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
}