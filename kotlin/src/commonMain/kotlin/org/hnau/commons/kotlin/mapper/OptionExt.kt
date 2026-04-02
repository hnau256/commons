package org.hnau.commons.kotlin.mapper

import arrow.core.Option
import arrow.core.toOption

fun <I, O> Mapper.Companion.option(
    base: Mapper<I, O>,
): Mapper<Option<I>, Option<O>> = Mapper(
    direct = { i -> i.map(base.direct) },
    reverse = { o -> o.map(base.reverse) },
)

fun <T> Mapper.Companion.optionToNullable(): Mapper<Option<T>, T?> = Mapper<Option<T>, T?>(
    direct = Option<T>::getOrNull,
    reverse = { nullable -> nullable.toOption() },
)
