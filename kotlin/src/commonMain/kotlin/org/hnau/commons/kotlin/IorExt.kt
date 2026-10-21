package org.hnau.commons.kotlin

import arrow.core.Ior

fun <L, R> Ior<L, R>.rightOrNull(): R? = fold(
    fa = { null },
    fb = ::it,
    fab = { _, right -> right },
)