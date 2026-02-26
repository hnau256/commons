package hnau.commons.kotlin.coroutines

import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.identity
import hnau.commons.kotlin.Loadable
import hnau.commons.kotlin.Loading
import hnau.commons.kotlin.Ready
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn

inline fun <T, R> Deferred<T>.toStateFlow(
    scope: CoroutineScope,
    initial: R,
    crossinline transform: (T) -> R,
): StateFlow<R> = flow {
    val result = await()
    val transformedResult = transform(result)
    emit(transformedResult)
}.stateIn(
    scope = scope,
    initialValue = initial,
    started = SharingStarted.Eagerly,
)

fun <T> Deferred<T>.toLoadableStateFlow(
    scope: CoroutineScope,
): StateFlow<Loadable<T>> = toStateFlow(
    scope = scope,
    transform = ::Ready,
    initial = Loading,
)

fun <T> Deferred<T>.toOptionStateFlow(
    scope: CoroutineScope,
): StateFlow<Option<T>> = toStateFlow(
    scope = scope,
    transform = ::Some,
    initial = None,
)

fun <T> Deferred<T>.toNullableStateFlow(
    scope: CoroutineScope,
): StateFlow<T?> = toStateFlow(
    scope = scope,
    transform = ::identity,
    initial = null,
)
