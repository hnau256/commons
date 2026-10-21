package org.hnau.commons.kotlin

inline fun <reified E : Enum<E>> E.next(
    offset: Int = 1,
): E {
    val entries = enumValues<E>()
    val index = (ordinal + offset) % entries.size
    return entries[index]
}

inline fun <reified E : Enum<E>> E.previous(
    offset: Int = 1,
): E = next(
    offset = -offset,
)