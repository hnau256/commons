package org.hnau.commons.kotlin.serialization

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import kotlinx.serialization.builtins.serializer
import org.hnau.commons.kotlin.mapper.Mapper
import org.hnau.commons.kotlin.mapper.stringToBigDecimal

object BigDecimalSerializer: MappingKSerializer<String, BigDecimal>(
    base = String.serializer(),
    mapper = Mapper.stringToBigDecimal,
)