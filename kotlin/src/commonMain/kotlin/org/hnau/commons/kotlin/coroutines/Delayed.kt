package org.hnau.commons.kotlin.coroutines

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.hnau.commons.kotlin.Loadable
import org.hnau.commons.kotlin.Loading
import org.hnau.commons.kotlin.Ready
import org.hnau.commons.kotlin.map

data class Delayed<out T>(
    val isInProgress: Boolean,
    val value: T,
)

inline fun <I, O> StateFlow<I>.mapStateDelayed(
    scope: CoroutineScope,
    crossinline transform: suspend (I) -> O,
): StateFlow<Loadable<Delayed<O>>> {
    val source: StateFlow<I> = this
    val result: MutableStateFlow<Loadable<Delayed<O>>> = MutableStateFlow(Loading)
    scope.launch {
        source.collectLatest { value ->
            result.update { lastOrInitializing ->
                lastOrInitializing.map { last ->
                    last.copy(isInProgress = true)
                }
            }
            val transformed = transform(value)
            result.value = Ready(
                Delayed(
                    isInProgress = false,
                    value = transformed,
                )
            )
        }
    }
    return result
}