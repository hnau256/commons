package org.hnau.commons.kotlin.mapper

import org.hnau.commons.kotlin.joinEscaped
import org.hnau.commons.kotlin.splitEscaped

fun Mapper.Companion.stringToStringsBySeparator(
    separator: Char,
    escape: Char = '\\',
): Mapper<String, List<String>> = Mapper(
    direct = { string ->
        string
            .splitEscaped(separator, escape)
    },
    reverse = { keyWithValue ->
        keyWithValue.joinEscaped(separator, escape)
    },
)
