package org.hnau.commons.app.model.app

import kotlinx.coroutines.CoroutineScope
import kotlinx.serialization.KSerializer
import org.hnau.commons.app.model.goback.GoBackHandler

data class RootModelFactory<S, M>(
    val skeletonSerializer: KSerializer<S>,
    val createDefaultSkeleton: () -> S,
    val createRootModel: (CoroutineScope, S) -> M,
    val extractGoBackHandler: (M) -> GoBackHandler,
)