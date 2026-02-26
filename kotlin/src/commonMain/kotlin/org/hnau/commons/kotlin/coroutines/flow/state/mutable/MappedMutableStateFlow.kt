package org.hnau.commons.kotlin.coroutines.flow.state.mutable

import org.hnau.commons.kotlin.coroutines.flow.state.mapState
import org.hnau.commons.kotlin.mapper.Mapper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ExperimentalForInheritanceCoroutinesApi
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

fun <I, O> MutableStateFlow<I>.mapMutableState(
    scope: CoroutineScope,
    transform: (I) -> O,
    compareAndSet: MutableStateFlow<I>.(expect: O, update: O) -> Boolean,
    set: MutableStateFlow<I>.(O) -> Unit,
): MutableStateFlow<O> = MappedMutableStateFlow(
    scope = scope,
    source = this,
    transform = transform,
    compareAndSet = compareAndSet,
    set = set,
)

fun <I, O> MutableStateFlow<I>.mapMutableState(
    scope: CoroutineScope,
    mapper: Mapper<I, O>,
): MutableStateFlow<O> = mapMutableState(
    scope = scope,
    transform = mapper.direct,
    compareAndSet = { expect, update ->
        compareAndSet(
            expect = mapper.reverse(expect),
            update = mapper.reverse(update),
        )
    },
    set = { value = mapper.reverse(it) },
)

@OptIn(ExperimentalForInheritanceCoroutinesApi::class)
private class MappedMutableStateFlow<I, O>(
    scope: CoroutineScope,
    private val source: MutableStateFlow<I>,
    private val transform: (I) -> O,
    private val compareAndSet: MutableStateFlow<I>.(expect: O, update: O) -> Boolean,
    private val set: MutableStateFlow<I>.(O) -> Unit,
) : MutableStateFlow<O> {

    private val immutable: StateFlow<O> = source.mapState(
        scope = scope,
        transform = transform,
    )
    override var value: O
        get() = immutable.value
        set(value) {
            source.set(value)
        }

    override fun compareAndSet(
        expect: O,
        update: O,
    ): Boolean = source.compareAndSet(
        expect,
        update,
    )

    override val subscriptionCount: StateFlow<Int>
        get() = source.subscriptionCount

    override suspend fun emit(
        value: O,
    ) {
        source.set(value)
    }

    override fun tryEmit(
        value: O,
    ): Boolean {
        source.set(value)
        return true
    }

    @ExperimentalCoroutinesApi
    override fun resetReplayCache() {
        source.resetReplayCache()
    }

    override val replayCache: List<O>
        get() = immutable.replayCache

    override suspend fun collect(
        collector: FlowCollector<O>,
    ): Nothing = immutable.collect(
        collector = collector,
    )
}