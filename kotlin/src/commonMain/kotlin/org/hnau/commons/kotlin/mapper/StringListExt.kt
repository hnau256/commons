package hnau.commons.kotlin.mapper

import hnau.commons.kotlin.joinEscaped
import hnau.commons.kotlin.splitEscaped

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
