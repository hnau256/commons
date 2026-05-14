package org.hnau.commons.app.test.app.model

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.Serializable
import org.hnau.commons.app.model.goback.GoBackHandler
import org.hnau.commons.app.model.stack.NonEmptyStack
import org.hnau.commons.app.model.stack.SkeletonWithModel
import org.hnau.commons.app.model.stack.goBackHandler
import org.hnau.commons.app.model.stack.modelsOnly
import org.hnau.commons.app.model.stack.withModels
import org.hnau.commons.gen.pipe.annotations.Pipe
import org.hnau.commons.gen.sealup.annotations.SealUp
import org.hnau.commons.gen.sealup.annotations.Variant

class RootStackModel(
    private val scope: CoroutineScope,
    private val skeleton: Skeleton,
    private val dependencies: Dependencies,
) {

    @Serializable
    data class Skeleton(
        val stack: MutableStateFlow<NonEmptyStack<RootStackElementSkeleton>> =
            MutableStateFlow(NonEmptyStack(ElementSkeleton.form(Config.default))),
    )

    @Pipe
    interface Dependencies

    @SealUp(
        variants = [
            Variant(
                type = FormModel::class,
                identifier = "form",
            ),
        ],
        wrappedValuePropertyName = "model",
        sealedInterfaceName = "RootStackElementModel",
    )
    interface Element {

        val goBackHandler: GoBackHandler

        companion object
    }

    @SealUp(
        variants = [
            Variant(
                type = FormModel.Skeleton::class,
                identifier = "form",
            ),
        ],
        wrappedValuePropertyName = "skeleton",
        sealedInterfaceName = "RootStackElementSkeleton",
        serializable = true,
    )
    interface ElementSkeleton {

        companion object
    }

    private val stackWithModels: StateFlow<NonEmptyStack<SkeletonWithModel<RootStackElementSkeleton, RootStackElementModel>>> =
        skeleton
            .stack
            .withModels(
                scope = scope,
                getKey = RootStackElementSkeleton::ordinal,
            ) { modelScope, skeleton ->
                createModel(
                    modelScope = modelScope,
                    skeleton = skeleton,
                )
            }

    private fun createModel(
        modelScope: CoroutineScope,
        skeleton: RootStackElementSkeleton,
    ): RootStackElementModel = skeleton.fold(
        ifForm = { formSkeleton ->
            Element.form(
                scope = modelScope,
                skeleton = formSkeleton,
            )
        },
    )

    val stack: StateFlow<NonEmptyStack<RootStackElementModel>> =
        stackWithModels.modelsOnly(scope)

    val goBackHandler: GoBackHandler = stackWithModels.goBackHandler(
        scope = scope,
        extractGoBackHandler = RootStackElementModel::goBackHandler,
        updateSkeletonStack = skeleton.stack::value::set,
    )
}