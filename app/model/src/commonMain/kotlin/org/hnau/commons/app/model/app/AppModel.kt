package org.hnau.commons.app.model.app

import kotlinx.coroutines.CoroutineScope
import kotlinx.serialization.json.Json
import org.hnau.commons.app.model.theme.palette.SystemPalettes
import org.hnau.commons.kotlin.mapper.Mapper
import org.hnau.commons.kotlin.mapper.toMapper

class AppModel<M, S>(
    scope: CoroutineScope,
    savedState: SavedState,
    seed: AppSeed<M, S>,
) {

    private val modelSkeletonMapper: Mapper<String, S> =
        json.toMapper(seed.skeletonSerializer)

    private val modelSkeleton: S = savedState
        .savedState
        ?.let(modelSkeletonMapper.direct)
        ?: seed.createDefaultSkeleton()

    val model: M = seed.createModel(scope, modelSkeleton)

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