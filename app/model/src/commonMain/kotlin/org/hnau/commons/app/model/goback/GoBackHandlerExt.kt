package org.hnau.commons.app.model.goback

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import org.hnau.commons.kotlin.coroutines.flow.state.flatMapWithScope
import org.hnau.commons.kotlin.coroutines.flow.state.mutable.toMutableStateFlowAsInitial
import org.hnau.commons.kotlin.foldNullable

val NeverGoBackHandler: GoBackHandler = MutableStateFlow(null)

fun GoBackHandler.withFallback(
    scope: CoroutineScope,
    createFallback: (CoroutineScope) -> GoBackHandler,
): GoBackHandler = flatMapWithScope(scope) { scope, goBackOrNull ->
    goBackOrNull.foldNullable(
        ifNotNull = { goBack -> goBack.toMutableStateFlowAsInitial() },
        ifNull = { createFallback(scope) }
    )
}
