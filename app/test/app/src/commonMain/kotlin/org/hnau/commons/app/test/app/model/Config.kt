package org.hnau.commons.app.test.app.model

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.integer.BigInteger

data class Config(
    val flag: Boolean,
    val decimal: BigDecimal,
    val integer: BigInteger,
    val text: String,
)