package hnau.commons.kotlin

import kotlinx.serialization.Serializable

@Serializable
data class KeyValue<out K, out V>(
    val key: K,
    val value: V,
) {

    inline fun <O> map(
        transform: (V) -> O,
    ): KeyValue<K, O> = KeyValue(
        key = key,
        value = transform(value),
    )

    inline fun <KO> mapKey(
        transform: (K) -> KO,
    ): KeyValue<KO, V> = KeyValue(
        key = transform(key),
        value = value,
    )
}

fun <K, V> Map.Entry<K, V>.toKeyValue(): KeyValue<K, V> = KeyValue(
    key = key,
    value = value
)