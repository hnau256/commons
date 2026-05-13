package org.hnau.commons.kotlin.serialization

import com.ionspin.kotlin.bignum.integer.BigInteger
import kotlinx.serialization.builtins.serializer
import org.hnau.commons.kotlin.mapper.Mapper
import org.hnau.commons.kotlin.mapper.stringToBigInteger

object BigIntegerSerializer: MappingKSerializer<String, BigInteger>(
    base = String.serializer(),
    mapper = Mapper.stringToBigInteger,
)