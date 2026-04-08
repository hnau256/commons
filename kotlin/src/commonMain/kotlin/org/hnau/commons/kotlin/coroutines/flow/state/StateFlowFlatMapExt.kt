package org.hnau.commons.kotlin.coroutines.flow.state

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import org.hnau.commons.kotlin.coroutines.flow.toFakeStateFlow
import org.hnau.commons.kotlin.ifNull

fun <I, O> StateFlow<I>.flatMapState(
    scope: CoroutineScope,
    transform: (I) -> StateFlow<O>,
): StateFlow<O> {
    val initialInput = this@flatMapState.value

    val initialFlow = transform(initialInput)

    val result = MutableStateFlow(initialFlow.value)

    val onceCache = OnceCache(initialInput, initialFlow)
    scope.launch {
        this@flatMapState.collectLatest { input ->
            onceCache
                .popValueIfKeyIsSameAsInitial(input)
                .ifNull { transform(input) }
                .collect { result.value = it }
        }
    }

    return result
}

fun <I, O> StateFlow<I>.flatMapWithScope(
    scope: CoroutineScope,
    transform: (CoroutineScope, I) -> StateFlow<O>,
): StateFlow<O> = this
    .scopedInState(scope)
    .flatMapState(scope) { (scope, value) ->
        transform(scope, value)
    }

@Suppress("DEPRECATION")
@OptIn(ExperimentalCoroutinesApi::class)
@Deprecated("Result of this function is fake StateFlow. Transform will be called for all collectors")
fun <I, O> StateFlow<I>.flatMapStateLite(
    transform: (I) -> StateFlow<O>,
): StateFlow<O> = flatMapLatest(transform)
    .toFakeStateFlow { transform(value).value }