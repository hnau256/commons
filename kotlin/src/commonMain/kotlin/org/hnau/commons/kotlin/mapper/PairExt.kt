package org.hnau.commons.kotlin.mapper

import org.hnau.commons.kotlin.KeyValue

fun <K, V> Mapper.Companion.pairToKeyValue(): Mapper<Pair<K, V>, KeyValue<K, V>> = Mapper(
    direct = { (key, value) ->
        KeyValue(
            key = key,
            value = value,
        )
    },
    reverse = { (key, value) -> key to value }
)