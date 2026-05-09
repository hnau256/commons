package org.hnau.commons.kotlin

import arrow.core.Either

val <T> Either<Nothing, T>.value: T
    get() = fold(
        ifLeft = { throwThisShouldNeverHappenError() },
        ifRight = ::it,
    )


val <T> Either<T, Nothing>.value: T
    get() = fold(
        ifLeft = ::it,
        ifRight = { throwThisShouldNeverHappenError() },
    )