package org.hnau.commons.kotlin

fun String.removePrefixOrNull(
    prefix: String,
): String? {
    if (prefix.isEmpty()) {
        return this
    }
    return startsWith(prefix).ifTrue { substring(prefix.length) }
}

fun String.removeSuffixOrNull(
    suffix: String,
): String? {
    if (suffix.isEmpty()) {
        return this
    }
    return endsWith(suffix).ifTrue { substring(0, length - suffix.length) }
}