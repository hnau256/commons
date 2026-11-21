package org.hnau.commons.app.model.app

import kotlinx.coroutines.CoroutineScope
import kotlinx.serialization.json.Json
import org.hnau.commons.app.model.goback.GoBackHandler
import org.hnau.commons.kotlin.mapper.Mapper
import org.hnau.commons.kotlin.mapper.toMapper

class RootModelWrapper<S, M>(
    scope: CoroutineScope,
    savedState: String?,
    root: RootModelFactory<S, M>,
) {

    private val modelSkeletonMapper: Mapper<String, S> =
        json.toMapper(root.skeletonSerializer)

    private val modelSkeleton: S = savedState
        ?.let(modelSkeletonMapper.direct)
        ?: root.createDefaultSkeleton()

    val model: M = root.createRootModel(scope, modelSkeleton)

    val goBackHandler: GoBackHandler = root.extractGoBackHandler(model)

    val savableState: String
        get() = modelSkeletonMapper.reverse(modelSkeleton)

    companion object {

        private val json: Json = Json {
            encodeDefaults = true
            ignoreUnknownKeys = true
            prettyPrint = true
        }
    }
}