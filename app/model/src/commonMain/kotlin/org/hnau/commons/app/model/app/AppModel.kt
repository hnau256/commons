package org.hnau.commons.app.model.app

import org.hnau.commons.kotlin.mapper.Mapper
import org.hnau.commons.kotlin.mapper.toMapper
import org.hnau.commons.app.model.app.utils.AppContext
import org.hnau.commons.app.model.goback.GoBackHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json

class AppModel<M, S>(
    scope: CoroutineScope,
    savedState: SavedState,
    appFilesDirProvider: AppFilesDirProvider,
    defaultTryUseSystemHue: Boolean,
    seed: AppSeed<M, S>,
) {

    private val modelSkeletonMapper: Mapper<String, S> =
        json.toMapper(seed.skeletonSerializer)

    private val modelSkeleton: S = savedState
        .savedState
        ?.let(modelSkeletonMapper.direct)
        ?: seed.createDefaultSkeleton()

    val appContext: AppContext = runBlocking {
        AppContext(
            scope = scope,
            defaultBrightness = seed.defaultBrightness,
            defaultTryUseSystemHue = defaultTryUseSystemHue,
            fallbackHue = seed.fallbackHue,
            filesDir = appFilesDirProvider.getAppFilesDir(),
        )
    }

    val model: M = seed.createModel(scope, appContext, modelSkeleton)

    val savableState: SavedState
        get() = modelSkeletonMapper.reverse(modelSkeleton).let(::SavedState)

    companion object {

        private val json: Json = Json {
            encodeDefaults = true
            ignoreUnknownKeys = true
            prettyPrint = true
        }
    }
}