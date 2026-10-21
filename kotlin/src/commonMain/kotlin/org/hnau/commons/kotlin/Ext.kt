package org.hnau.commons.kotlin

inline fun <T> T?.ifNull(
    ifNull: () -> T,
): T = this ?: ifNull()

inline fun <reified O> Any?.castOrElse(
    elseAction: () -> O,
): O = when (this) {
    is O -> this
    else -> elseAction()
}

operator fun <R> ((Unit) -> R).invoke(): R = invoke(Unit)

inline fun <reified O> Any?.castOrNull(): O? = this as? O

inline fun <reified O> Any?.castOrThrow(): O = this as O

@Suppress("NOTHING_TO_INLINE")
inline fun <T> it(it: T): T = it
