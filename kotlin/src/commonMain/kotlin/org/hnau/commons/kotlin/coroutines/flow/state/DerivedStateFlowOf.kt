package org.hnau.commons.kotlin.coroutines.flow.state

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.hnau.commons.kotlin.coroutines.createChild


fun <R> derivedStateFlowOf(
    scope: CoroutineScope,
    block: StateFlowStateProvider.() -> R,
): StateFlow<R> {

    fun createState(): Expirable<R> {
        val scope = scope.createChild()
        val dependencies: MutableList<Deferred<Unit>> = mutableListOf()
        val stateFlowStateProvider = StateFlowStateProviderImpl(
            scope = scope,
            onNewDependency = dependencies::add,
        )
        val state = stateFlowStateProvider.block()

        dependencies.forEach { dependency ->
            scope.launch {
                dependency.await()
                scope.cancel()
            }
        }

        return Expirable(
            value = state,
            actualJob = scope.launch { awaitCancellation() },
        )
    }

    val result = MutableStateFlow(
        value = createState()
    )

    scope.launch {
        while (isActive) {
            val state = result.value
            state.actualJob.join()
            result.value = createState()
        }
    }

    @Suppress("DEPRECATION")
    return result.mapStateLite(Expirable<R>::value)
}

interface StateFlowStateProvider {

    val <T> StateFlow<T>.state: T
}

private data class Expirable<out T>(
    val value: T,
    val actualJob: Job,
)

private class StateFlowStateProviderImpl(
    private val scope: CoroutineScope,
    private val onNewDependency: (Deferred<Unit>) -> Unit,
) : StateFlowStateProvider {

    override val <T> StateFlow<T>.state: T
        get() {
            val result = value
            onNewDependency(
                scope.async {
                    first { it != result }
                    Unit
                }
            )
            return result
        }
}