package org.hnau.commons.app.model.input

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import org.hnau.commons.kotlin.it
import kotlin.jvm.JvmName

inline fun <IN, ERROR_THIS, ERROR_OTHER, ERROR, MIDDLE, OUT> InputParser<IN, ERROR_THIS, MIDDLE>.with(
    other: InputParser<MIDDLE, ERROR_OTHER, OUT>,
    crossinline ifThisError: (ERROR_THIS) -> ERROR,
    crossinline ifOtherError: (ERROR_OTHER) -> ERROR,
): InputParser<IN, ERROR, OUT> = InputParser(
    parse = { string ->
        parse(string).fold(
            ifLeft = { inError -> ifThisError(inError).left() },
            ifRight = {
                other.parse(it).fold(
                    ifLeft = { outError -> ifOtherError(outError).left() },
                    ifRight = { result -> result.right() }
                )
            }
        )
    },
)

@JvmName("alwaysSuccessPlus")
operator fun <IN, ERROR, MIDDLE, OUT> InputParser<IN, Nothing, MIDDLE>.plus(
    other: InputParser<MIDDLE, ERROR, OUT>,
): InputParser<IN, ERROR, OUT> = with(
    other = other,
    ifThisError = ::it,
    ifOtherError = ::it
)

@JvmName("plusAlwaysSuccess")
operator fun <IN, ERROR, MIDDLE, OUT> InputParser<IN, ERROR, MIDDLE>.plus(
    other: InputParser<MIDDLE, Nothing, OUT>,
): InputParser<IN, ERROR, OUT> = with(
    other = other,
    ifThisError = ::it,
    ifOtherError = ::it
)

operator fun <IN, ERROR_THIS, ERROR_OTHER, MIDDLE, OUT> InputParser<IN, ERROR_THIS, MIDDLE>.plus(
    other: InputParser<MIDDLE, ERROR_OTHER, OUT>,
): InputParser<IN, Either<ERROR_THIS, ERROR_OTHER>, OUT> = with(
    other = other,
    ifThisError = { error -> error.left() },
    ifOtherError = { error -> error.right() }
)