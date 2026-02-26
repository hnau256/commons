package org.hnau.commons.kotlin.coroutines.flow.state

import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.identity
import arrow.core.toOption
import org.hnau.commons.kotlin.coroutines.createChild
import org.hnau.commons.kotlin.foldNullable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

fun <I, R, S : Stickable<I, R>> StateFlow<I>.stick(
    scope: CoroutineScope,
    createStickable: (
        stickableScope: CoroutineScope,
        initialValue: I,
    ) -> S,
): StateFlow<R> {
    val createScopedStickable: (I) -> ScopedStickable<I, R, S> = { initialValue: I ->
        val stickableScope = scope.createChild()
        ScopedStickable(
            cancel = { stickableScope.cancel() },
            stickable = createStickable(stickableScope, initialValue),
        )
    }

    @Suppress("DEPRECATION")
    return this
        .runningFoldState(
            scope = scope,
            createInitial = createScopedStickable,
            operation = { item, newValue ->
                if (item.stickable.tryUpdateValue(newValue)) {
                    return@runningFoldState item
                }
                item.cancel()
                createScopedStickable(newValue)
            },
        )
        .mapStateLite { it.stickable.result }
}

interface Stickable<in I, out R> {

    fun tryUpdateValue(newValue: I): Boolean

    val result: R

    companion object
}

fun <I, R> Stickable(
    tryUpdateValue: (newValue: I) -> Boolean,
    result: R,
): Stickable<I, R> = object : Stickable<I, R> {

    override fun tryUpdateValue(
        newValue: I,
    ): Boolean = tryUpdateValue.invoke(newValue)

    override val result: R get() = result
}

fun <R> Stickable.Companion.predetermined(
    result: R,
): Stickable<Any?, R> = Stickable(
    tryUpdateValue = { false },
    result = result,
)

private val nullableStickable: Stickable<Any?, Nothing?> =
    Stickable.predetermined(null)

val Stickable.Companion.nullable: Stickable<Any?, Nothing?>
    get() = nullableStickable

fun <I, M, R> Stickable.Companion.stateFlow(
    initial: M,
    tryUseNext: (I) -> Option<M>,
    createResult: (value: StateFlow<M>) -> R,
): Stickable<I, R> {
    val value: MutableStateFlow<M> = MutableStateFlow(initial)
    return Stickable(
        tryUpdateValue = { newValue ->
            when (val next = tryUseNext(newValue)) {
                None -> false
                is Some -> {
                    value.value = next.value
                    true
                }
            }
        },
        result = createResult(value),
    )
}

fun <I, O> Stickable.Companion.stateFlow(
    initial: O,
    tryUseNext: (I) -> Option<O>,
): Stickable<I, StateFlow<O>> = stateFlow(
    initial = initial,
    tryUseNext = tryUseNext,
    createResult = ::identity,
)

fun <T : Any> Stickable.Companion.stateFlowOfNotNull(
    initial: T,
): Stickable<T?, StateFlow<T>> = stateFlow(
    initial = initial,
    tryUseNext = { next -> next.toOption() },
)

@PublishedApi
internal data class ScopedStickable<in I, out R, S : Stickable<I, R>>(
    val cancel: () -> Unit,
    val stickable: S,
)

fun <T : Any> StateFlow<T?>.stickNotNull(
    scope: CoroutineScope,
): StateFlow<StateFlow<T>?> = stick(scope) { _, initialOrNull ->
    initialOrNull.foldNullable(
        ifNull = { Stickable.nullable },
        ifNotNull = { value -> Stickable.stateFlowOfNotNull(value) },
    )
}
