package org.hnau.commons.app.model.stack

import arrow.core.identity
import org.hnau.commons.app.model.goback.GoBackHandler
import org.hnau.commons.kotlin.coroutines.flow.state.flatMapWithScope
import org.hnau.commons.kotlin.coroutines.flow.state.mapReusable
import org.hnau.commons.kotlin.coroutines.flow.state.mapState
import org.hnau.commons.kotlin.coroutines.flow.state.mapWithScope
import org.hnau.commons.kotlin.foldNullable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow

data class SkeletonWithModel<out S, out M>(
    val skeleton: S,
    val model: M,
)

inline fun <S, M, K> StateFlow<NonEmptyStack<S>>.withModels(
    scope: CoroutineScope,
    crossinline getKey: (S) -> K,
    crossinline createModel: (CoroutineScope, S) -> M,
): StateFlow<NonEmptyStack<SkeletonWithModel<S, M>>> = mapReusable(
    scope = scope,
) { skeletons ->
    skeletons.map { skeleton ->
        SkeletonWithModel(
            skeleton = skeleton,
            model = getOrPutItem(
                key = getKey(skeleton),
            ) { modelScope ->
                createModel(modelScope, skeleton)
            }
        )
    }
}

fun <S, M> StateFlow<NonEmptyStack<SkeletonWithModel<S, M>>>.modelsOnly(
    scope: CoroutineScope,
): StateFlow<NonEmptyStack<M>> = mapState(scope) { stack ->
    stack.map(SkeletonWithModel<*, M>::model)
}

inline fun <S, M> StateFlow<NonEmptyStack<SkeletonWithModel<S, M>>>.goBackHandler(
    scope: CoroutineScope,
    crossinline extractGoBackHandler: (M) -> GoBackHandler,
    crossinline updateSkeletonStack: (NonEmptyStack<S>) -> Unit,
): GoBackHandler = flatMapWithScope(scope) { scope, stack ->
    stack
        .tail
        .model
        .let(extractGoBackHandler)
        .mapWithScope(scope) { scope, tailGoBackOrNull ->
            tailGoBackOrNull.foldNullable(
                ifNotNull = ::identity,
                ifNull = {
                    stack
                        .tryDropLast()
                        ?.map(SkeletonWithModel<S, *>::skeleton)
                        ?.let { newStack ->
                            { updateSkeletonStack(newStack) }
                        }
                }
            )
        }
}
