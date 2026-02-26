package hnau.commons.kotlin.coroutines

import hnau.commons.kotlin.coroutines.flow.state.mapState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class InProgressRegistry(
    scope: CoroutineScope,
) {

    private val activitiesCount: MutableStateFlow<Int> = MutableStateFlow(0)

    val inProgress: StateFlow<Boolean> = activitiesCount
        .mapState(scope) { activitiesCount -> activitiesCount > 0 }

    @PublishedApi
    internal fun incActivitiesCount(delta: Int) {
        activitiesCount.update { it + delta }
    }

    inline fun <R> executeRegistered(
        action: () -> R,
    ): R = try {
        incActivitiesCount(1)
        action()
    } finally {
        incActivitiesCount(-1)
    }
}
