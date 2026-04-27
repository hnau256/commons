package org.hnau.commons.app.test.app.model

import kotlinx.coroutines.CoroutineScope
import kotlinx.serialization.Serializable
import org.hnau.commons.gen.pipe.annotations.Pipe

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

}