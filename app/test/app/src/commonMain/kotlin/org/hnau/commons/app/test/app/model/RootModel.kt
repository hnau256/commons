package org.hnau.commons.app.test.app.model

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.serialization.Serializable
import org.hnau.commons.app.model.goback.GoBackHandler
import org.hnau.commons.app.model.goback.NeverGoBackHandler
import org.hnau.commons.gen.pipe.annotations.Pipe
import org.hnau.commons.kotlin.coroutines.actionOrCancelIfExecuting
import kotlin.time.Duration.Companion.seconds

class RootModel(
    scope: CoroutineScope,
    dependencies: Dependencies,
    skeleton: Skeleton,
) {

    @Pipe
    interface Dependencies {

        companion object
    }

    @Serializable
    data class Skeleton(
        val a: Int = 0,
    )

    val task = actionOrCancelIfExecuting(scope) {
        delay(5.seconds)
    }

    val goBackHandler: GoBackHandler
        get() = NeverGoBackHandler
}