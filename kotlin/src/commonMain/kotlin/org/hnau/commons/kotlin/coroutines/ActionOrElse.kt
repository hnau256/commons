package org.hnau.commons.kotlin.coroutines

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.hnau.commons.kotlin.coroutines.flow.state.mapState
import org.hnau.commons.kotlin.foldNullable


sealed interface CancelOrInProgress {

    data class Cancel(
        val cancel: () -> Unit,
    ) : CancelOrInProgress

    data object InProgress : CancelOrInProgress

}

sealed interface ActionOrElse<out I, out E : CancelOrInProgress> {

    data class Action<I>(
        val action: (I) -> Unit,
    ) : ActionOrElse<I, Nothing>

    data class Else<out E : CancelOrInProgress>(
        val cancelOrInProgress: E,
    ) : ActionOrElse<Nothing, E>
}


fun ActionOrElse.Else<CancelOrInProgress.Cancel>.cancel() {
    cancelOrInProgress.cancel()
}

fun <A> operationOrCancelIfExecuting(
    scope: CoroutineScope,
    operation: suspend (A) -> Unit,
): StateFlow<ActionOrElse<A, CancelOrInProgress.Cancel>> =
    operationOrElseIfExecuting<A, CancelOrInProgress.Cancel>(
        scope = scope,
        operation = operation,
    )

fun actionOrCancelIfExecuting(
    scope: CoroutineScope,
    operation: suspend () -> Unit,
): StateFlow<ActionOrElse<Unit, CancelOrInProgress.Cancel>> = operationOrCancelIfExecuting(
    scope = scope,
    operation = { _: Unit -> operation() },
)

fun <A> operationOrInProgressIfExecuting(
    scope: CoroutineScope,
    operation: suspend (A) -> Unit,
): StateFlow<ActionOrElse<A, CancelOrInProgress.InProgress>> =
    operationOrElseIfExecuting<A, CancelOrInProgress.InProgress>(
        scope = scope,
        operation = operation,
    )

fun actionOrInProgressIfExecuting(
    scope: CoroutineScope,
    operation: suspend () -> Unit,
): StateFlow<ActionOrElse<Unit, CancelOrInProgress.InProgress>> = operationOrInProgressIfExecuting(
    scope = scope,
    operation = { _: Unit -> operation() },
)

private inline fun <A, reified E : CancelOrInProgress> operationOrElseIfExecuting(
    scope: CoroutineScope,
    noinline operation: suspend (A) -> Unit,
): StateFlow<ActionOrElse<A, E>> = MutableStateFlow<CoroutineScope?>(null)
    .let { operationScopeStateFlow ->
        operationScopeStateFlow.mapState(scope) { operationScopeOrNull ->
            operationScopeOrNull.foldNullable(
                ifNotNull = { operationScope ->
                    val cancelOrInProgress: E = when (E::class) {
                        CancelOrInProgress.Cancel::class -> CancelOrInProgress.Cancel { operationScope.cancel() }
                        else -> CancelOrInProgress.InProgress
                    } as E
                    ActionOrElse.Else(cancelOrInProgress)
                },
                ifNull = {
                    ActionOrElse.Action { argument ->
                        val operationScope = scope.createChild()
                        operationScopeStateFlow.value = operationScope
                        operationScope.launch {
                            try {
                                operation(argument)
                            } finally {
                                operationScopeStateFlow.value = null
                            }
                        }
                    }
                }
            )
        }
    }