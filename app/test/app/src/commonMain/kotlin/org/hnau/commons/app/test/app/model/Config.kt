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
    val decimal: Float,
    val integer: Int,
    val text: String,
    val scheme: Scheme,
) {

    enum class Scheme { Http, Https }

    companion object {

        val default = Config(
            flag = false,
            decimal = 456.789f,
            integer = 123,
            text = "QWERTY",
            scheme = Scheme.Https,
        )
    }
}