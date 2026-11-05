package org.hnau.commons.app.model.app

import kotlinx.coroutines.CoroutineScope
import kotlinx.serialization.KSerializer

data class AppSeed<S, M>(
    val skeletonSerializer: KSerializer<S>,
    val createDefaultSkeleton: () -> S,
    val createModel: (CoroutineScope, S) -> M,
)