package org.hnau.commons.kotlin.coroutines.flow

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transformLatest

@OptIn(ExperimentalCoroutinesApi::class)
fun <T> Flow<T>.onEachLatest(
    action: suspend (T) -> Unit,
): Flow<T> = transformLatest { value ->
    action(value)
    emit(value)
}
