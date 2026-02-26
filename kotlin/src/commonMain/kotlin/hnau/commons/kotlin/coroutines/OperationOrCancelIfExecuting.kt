package hnau.commons.kotlin.coroutines

import hnau.commons.kotlin.coroutines.flow.state.mapState
import hnau.commons.kotlin.foldNullable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


sealed interface OperationOrCancel<I> {

    data class Operation<I>(
        val operation: (I) -> Unit,
    ) : OperationOrCancel<I>

    data class Cancel<I>(
        val cancel: () -> Unit,
    ) : OperationOrCancel<I>
}


fun <A> operationOrCancelIfExecuting(
    scope: CoroutineScope,
    operation: suspend (A) -> Unit,
): StateFlow<OperationOrCancel<A>> = MutableStateFlow<CoroutineScope?>(null)
    .let { operationScopeStateFlow ->
        operationScopeStateFlow.mapState(scope) { operationScopeOrNull ->
            operationScopeOrNull.foldNullable(
                ifNotNull = { operationScope ->
                    OperationOrCancel.Cancel { operationScope.cancel() }
                },
                ifNull = {
                    OperationOrCancel.Operation { argument ->
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

fun <A> operationOrNullIfExecuting(
    scope: CoroutineScope,
    operation: suspend (A) -> Unit,
): StateFlow<((A) -> Unit)?> = operationOrCancelIfExecuting(
    scope = scope,
    operation = operation
).mapState(scope) { operationOrCancel ->
    when (operationOrCancel) {
        is OperationOrCancel.Cancel<A> -> null
        is OperationOrCancel.Operation<A> -> operationOrCancel.operation
    }
}


sealed interface ActionOrCancel {

    data class Action(
        val action: () -> Unit,
    ) : ActionOrCancel

    data class Cancel(
        val cancel: () -> Unit,
    ) : ActionOrCancel
}


fun actionOrCancelIfExecuting(
    scope: CoroutineScope,
    action: suspend () -> Unit,
): StateFlow<ActionOrCancel> = operationOrCancelIfExecuting<Unit>(
    scope = scope,
    operation = { action() },
).mapState(scope) { operationOrCancel ->
    when (operationOrCancel) {
        is OperationOrCancel.Cancel<Unit> -> ActionOrCancel.Cancel(
            cancel = operationOrCancel.cancel,
        )

        is OperationOrCancel.Operation<Unit> -> ActionOrCancel.Action(
            action = { operationOrCancel.operation(Unit) },
        )
    }
}


fun actionOrNullIfExecuting(
    scope: CoroutineScope,
    action: suspend () -> Unit,
): StateFlow<(() -> Unit)?> = actionOrCancelIfExecuting(
    scope = scope,
    action = action
).mapState(scope) { actionOrCancel ->
    when (actionOrCancel) {
        is ActionOrCancel.Cancel -> null
        is ActionOrCancel.Action -> actionOrCancel.action
    }
}