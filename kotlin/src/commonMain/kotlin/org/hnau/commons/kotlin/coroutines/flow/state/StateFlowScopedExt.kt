package org.hnau.commons.kotlin.coroutines.flow.state

import org.hnau.commons.kotlin.coroutines.createChild
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.StateFlow

fun <T> StateFlow<T>.scopedInState(
    parentScope: CoroutineScope,
    createChildJob: (parentJob: Job) -> Job = ::SupervisorJob,
): StateFlow<Scoped<T>> = runningFoldState(
    scope = parentScope,
    createInitial = { value ->
        val initialScope = parentScope.createChild(createChildJob = createChildJob)
        Scoped(initialScope, value)
    },
    operation = { previousScopeWithValue, nextValue ->
        previousScopeWithValue.scope.cancel()
        val nextScope = parentScope.createChild(createChildJob = createChildJob)
        Scoped(nextScope, nextValue)
    },
)

data class Scoped<T>(
    val scope: CoroutineScope,
    val value: T,
)

fun <I, O> Scoped<I>.map(
    transform: (I) -> O,
): Scoped<O> = Scoped(
    scope = scope,
    value = value.let(transform),
)
