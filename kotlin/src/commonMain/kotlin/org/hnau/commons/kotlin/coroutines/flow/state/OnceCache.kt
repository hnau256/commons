package org.hnau.commons.kotlin.coroutines.flow.state

import org.hnau.commons.kotlin.KeyValue

class OnceCache<K, V : Any>(
    key: K,
    value: V,
) {

    private var initial: KeyValue<K, V>? = KeyValue(key, value)

    fun popValueIfKeyIsSameAsInitial(
        key: K,
    ): V? = initial?.let { localInitial ->
        initial = null
        localInitial
            .takeIf { it.key === key }
            ?.value
    }
}
