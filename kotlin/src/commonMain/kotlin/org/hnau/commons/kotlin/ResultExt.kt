package org.hnau.commons.kotlin

import arrow.core.Either
import arrow.core.left
import arrow.core.right

fun <T> Result<T>.toEither(): Either<Throwable, T> = fold(
    onSuccess = { value -> value.right() },
    onFailure = { error -> error.left() }
)