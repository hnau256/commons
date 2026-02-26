package hnau.commons.kotlin

import kotlin.jvm.JvmName

inline fun <T, R> T?.foldNullable(
    ifNull: () -> R,
    ifNotNull: (T & Any) -> R,
): R = when (this) {
    null -> ifNull()
    else -> ifNotNull(this)
}

@JvmName("foldNullableNonNull")
@Suppress("unused")
@Deprecated(
    message = "Receiver is already non-nullable. Just use 'ifNotNull' block directly or standard scoping functions.",
    level = DeprecationLevel.ERROR,
    replaceWith = ReplaceWith("ifNotNull(this)")
)
fun <T : Any, R> T.foldNullable(
    ifNull: () -> R,
    ifNotNull: (T) -> R
): R = ifNotNull(this)