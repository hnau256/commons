package org.hnau.commons.kotlin.coroutines.flow.state

import org.hnau.commons.kotlin.coroutines.flow.toFakeStateFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

fun <I1, I2, R> combineState(
    scope: CoroutineScope,
    first: StateFlow<I1>,
    second: StateFlow<I2>,
    combine: (I1, I2) -> R,
): StateFlow<R> {

    val firstInitial = first.value
    val secondInitial = second.value

    val firstChecker: OnceCache<I1, Unit> =
        OnceCache(firstInitial, Unit)

    val secondChecker: OnceCache<I2, Unit> =
        OnceCache(secondInitial, Unit)

    val result = MutableStateFlow(combine(firstInitial, secondInitial))

    scope.launch {
        combine(
            flow = first,
            flow2 = second,
        ) { firstValue, secondValue ->

            val firstIsSameAsInitial = firstChecker.popValueIfKeyIsSameAsInitial(firstValue) != null
            val secondIsSameAsInitial =
                secondChecker.popValueIfKeyIsSameAsInitial(secondValue) != null

            if (firstIsSameAsInitial && secondIsSameAsInitial) {
                return@combine
            }

            result.value = combine(firstValue, secondValue)
        }.collect()
    }

    return result
}

fun <I, O, R> StateFlow<I>.combineStateWith(
    scope: CoroutineScope,
    other: StateFlow<O>,
    combine: (I, O) -> R,
): StateFlow<R> = combineState(
    scope = scope,
    first = this,
    second = other,
    combine = combine,
)

@Suppress("DEPRECATION")
@Deprecated("Result of this function is fake StateFlow. Combine will be called for all collectors")
fun <I1, I2, R> combineStateLite(
    first: StateFlow<I1>,
    second: StateFlow<I2>,
    combine: (I1, I2) -> R,
): StateFlow<R> = combine(
    flow = first,
    flow2 = second,
    transform = combine
).toFakeStateFlow {
    combine(
        first.value,
        second.value,
    )
}
