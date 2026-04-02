package org.hnau.commons.kotlin.mapper

import kotlinx.serialization.KSerializer
import kotlinx.serialization.cbor.Cbor

fun <T> Cbor.toMapper(
    serializer: KSerializer<T>,
): Mapper<ByteArray, T> = Mapper(
    direct = { bytes -> decodeFromByteArray(serializer, bytes) },
    reverse = { value -> encodeToByteArray(serializer, value) },
)
