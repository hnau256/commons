package org.hnau.commons.app.model.preferences

import arrow.core.Option
import arrow.core.Some
import arrow.core.getOrElse
import kotlinx.coroutines.CoroutineScope
import org.hnau.commons.kotlin.coroutines.flow.state.mapState
import org.hnau.commons.kotlin.mapper.Mapper

fun <I, O> Preference<Option<I>>.map(
    scope: CoroutineScope,
    mapper: Mapper<I, O>,
): Preference<Option<O>> = mapOption(
    scope = scope,
    mapper = Mapper(
        direct = { it.map(mapper.direct) },
        reverse = { it.map(mapper.reverse) }
    )
)

fun <I, O> Preference<Option<I>>.mapOption(
    scope: CoroutineScope,
    mapper: Mapper<Option<I>, O>,
): Preference<O> = Preference<O>(
    value = value.mapState(scope) { valueOrNone ->
        mapper.direct(valueOrNone)
    },
    update = { newValue ->
        update(newValue.let(mapper.reverse))
    }
)

inline fun <T> Preference<Option<T>>.withDefault(
    scope: CoroutineScope,
    crossinline default: () -> T,
): Preference<T> = mapOption(
    scope = scope,
    mapper = Mapper(
        direct = { valueOrNone ->
            valueOrNone.getOrElse(default)
        },
        reverse = ::Some
    )
)