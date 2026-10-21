package org.hnau.commons.app.test.app.model

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow
import org.hnau.commons.app.model.goback.GoBackHandler
import org.hnau.commons.app.model.goback.NeverGoBackHandler
import org.hnau.commons.gen.pipe.annotations.Pipe
import org.hnau.commons.kotlin.coroutines.ActionOrElse
import org.hnau.commons.kotlin.coroutines.CancelOrInProgress
import org.hnau.commons.kotlin.coroutines.actionOrCancelIfExecuting
import kotlin.time.Duration.Companion.seconds

class ActionModel(
    scope: CoroutineScope,
    private val dependencies: Dependencies,
    val editConfig: () -> Unit,
) {

    @Pipe
    interface Dependencies {

        val config: StateFlow<Config>
    }

    val config: StateFlow<Config>
        get() = dependencies.config

    val doAction: StateFlow<ActionOrElse<Unit, CancelOrInProgress.Cancel>> = actionOrCancelIfExecuting(scope) {
        delay(5.seconds)
    }

    val goBackHandler: GoBackHandler
        get() = NeverGoBackHandler
}