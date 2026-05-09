package org.hnau.commons.kotlin

import arrow.core.Either
import kotlin.jvm.JvmName

val <T> Either<Nothing, T>.value: T
    @JvmName("getEitherWitchLeftIsNothingValue")
    get() = fold(
        ifLeft = { throwThisShouldNeverHappenError() },
        ifRight = ::it,
    )


val <T> Either<T, Nothing>.value: T
    @JvmName("getEitherWitchRightIsNothingValue")
    get() = fold(
        ifLeft = ::it,
        ifRight = { throwThisShouldNeverHappenError() },
    )