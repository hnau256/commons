@file:UseSerializers(
    BigDecimalSerializer::class,
    BigIntegerSerializer::class,
)

package org.hnau.commons.app.test.app.model

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.integer.BigInteger
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import org.hnau.commons.kotlin.serialization.BigDecimalSerializer
import org.hnau.commons.kotlin.serialization.BigIntegerSerializer

@Serializable
data class Config(
    val flag: Boolean,
    val decimal: BigDecimal,
    val integer: BigInteger,
    val text: String,
) {

    companion object {

        val default = Config(
            flag = false,
            decimal = BigDecimal.fromDouble(456.789),
            integer = BigInteger.fromInt(123),
            text = "QWERTY"
        )
    }
}