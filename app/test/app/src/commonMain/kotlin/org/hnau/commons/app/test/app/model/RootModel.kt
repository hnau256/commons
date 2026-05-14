package org.hnau.commons.app.test.app.model

import kotlinx.coroutines.CoroutineScope
import kotlinx.serialization.Serializable
import org.hnau.commons.app.model.goback.GoBackHandler
import org.hnau.commons.gen.pipe.annotations.Pipe

class RootModel(
    scope: CoroutineScope,
    dependencies: Dependencies,
    skeleton: Skeleton,
) {

    @Pipe
    interface Dependencies {

        fun stack(): RootStackModel.Dependencies

        companion object
    }

    @Serializable
    data class Skeleton(
        val stack: RootStackModel.Skeleton = RootStackModel.Skeleton(),
    )

    val stack = RootStackModel(
        scope = scope,
        skeleton = skeleton.stack,
        dependencies = dependencies.stack(),
    )

    val goBackHandler: GoBackHandler
        get() = stack.goBackHandler
}