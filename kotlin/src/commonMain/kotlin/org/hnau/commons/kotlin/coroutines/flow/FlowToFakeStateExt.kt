package org.hnau.commons.kotlin.coroutines.flow

import kotlinx.coroutines.ExperimentalForInheritanceCoroutinesApi
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.StateFlow

@OptIn(ExperimentalForInheritanceCoroutinesApi::class)
@Deprecated("Result of this function is fake StateFlow")
internal fun <T> Flow<T>.toFakeStateFlow(
    getCurrentValue: () -> T,
): StateFlow<T> = object : StateFlow<T> {

    override val value: T
        get() = getCurrentValue()
    override val replayCache: List<T>
        get() = listOf(value)

    override suspend fun collect(
        collector: FlowCollector<T>,
    ): Nothing {
        this@toFakeStateFlow.collect(collector)
        awaitCancellation()
    }
}