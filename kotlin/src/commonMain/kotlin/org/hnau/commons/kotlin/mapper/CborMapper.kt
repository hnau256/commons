package org.hnau.commons.kotlin.mapper

import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json.Default.decodeFromString
import kotlinx.serialization.json.Json.Default.encodeToString

fun <T> Cbor.toMapper(
    serializer: KSerializer<T>,
): Mapper<String, T> = Mapper(
    direct = { json -> decodeFromString(serializer, json) },
    reverse = { value -> encodeToString(serializer, value) },
)
