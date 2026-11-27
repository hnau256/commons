package org.hnau.commons.app.projector.utils

import androidx.compose.runtime.State
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.collectLatest

/*
suspend fun <T> State<T>.observe(
    onChanged: suspend (T) -> Unit,
): Nothing = coroutineScope {

    val observer = SnapshotStateObserver { task -> task() }.apply { start() }

    var currentJob: Job? = null

    fun startObserving() {
        if (!isActive) return

        observer.observeReads(
            scope = observer,
            onValueChangedForScope = {
                currentJob?.cancel()
                currentJob = launch { onChanged(value) }
                startObserving()
            },
            block = { value }
        )
    }

    try {
        currentJob = launch { onChanged(value) }
        startObserving()
        awaitCancellation()
    } finally {
        observer.stop()
        observer.clear()
        currentJob?.cancel()
    }
}*/

suspend fun <T> State<T>.observe(
    onChanged: suspend (T) -> Unit,
): Nothing {
    snapshotFlow { value }.collectLatest { onChanged(it) }
    awaitCancellation()
}