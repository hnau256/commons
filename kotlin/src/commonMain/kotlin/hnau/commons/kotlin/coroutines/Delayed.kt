package hnau.commons.kotlin.coroutines

import hnau.commons.kotlin.Loadable
import hnau.commons.kotlin.Loading
import hnau.commons.kotlin.Ready
import hnau.commons.kotlin.map
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

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