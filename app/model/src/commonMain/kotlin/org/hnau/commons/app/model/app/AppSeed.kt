package org.hnau.commons.app.model.app

import org.hnau.commons.app.model.utils.Hue
import org.hnau.commons.app.model.theme.ThemeBrightness
import kotlinx.coroutines.CoroutineScope
import kotlinx.serialization.KSerializer

data class AppSeed<M, S>(
    val defaultBrightness: ThemeBrightness? = null,
    val fallbackHue: Hue,
    val skeletonSerializer: KSerializer<S>,
    val createDefaultSkeleton: () -> S,
    val createModel: (CoroutineScope, AppContext, S) -> M,
)