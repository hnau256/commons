package org.hnau.commons.kotlin.coroutines.flow.state

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

fun <I, O> StateFlow<I>.runningFoldState(
    scope: CoroutineScope,
    createInitial: (I) -> O,
    operation: suspend (O, I) -> O,
): StateFlow<O> {
    val initialValue: I = value
    val onceCache: OnceCache<I, Unit> = OnceCache(initialValue, Unit)
    val result: MutableStateFlow<O> = MutableStateFlow(createInitial(initialValue))

    scope.launch {
        collect { newValue: I ->
            val isValueSameAsInitial: Boolean =
                onceCache.popValueIfKeyIsSameAsInitial(newValue) != null

            if (isValueSameAsInitial) {
                return@collect
            }

            result.value = operation(result.value, newValue)
        }
    }

    return result
}

fun <T> StateFlow<T>.runningFoldState(
    scope: CoroutineScope,
    operation: suspend (T, T) -> T,
): StateFlow<T> = runningFoldState(
    scope = scope,
    createInitial = { it },
    operation = operation,
)

