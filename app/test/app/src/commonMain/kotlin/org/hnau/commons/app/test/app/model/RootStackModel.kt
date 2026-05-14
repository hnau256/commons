package org.hnau.commons.app.test.app.model

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.hnau.commons.app.model.goback.GoBackHandler
import org.hnau.commons.app.model.preferences.Preference
import org.hnau.commons.app.model.preferences.Preferences
import org.hnau.commons.app.model.preferences.map
import org.hnau.commons.app.model.preferences.withDefault
import org.hnau.commons.app.model.stack.NonEmptyStack
import org.hnau.commons.app.model.stack.SkeletonWithModel
import org.hnau.commons.app.model.stack.goBackHandler
import org.hnau.commons.app.model.stack.modelsOnly
import org.hnau.commons.app.model.stack.push
import org.hnau.commons.app.model.stack.withModels
import org.hnau.commons.gen.pipe.annotations.Pipe
import org.hnau.commons.gen.sealup.annotations.SealUp
import org.hnau.commons.gen.sealup.annotations.Variant
import org.hnau.commons.kotlin.mapper.toMapper

class RootStackModel(
    private val scope: CoroutineScope,
    private val skeleton: Skeleton,
    private val dependencies: Dependencies,
) {

    @Serializable
    data class Skeleton(
        val stack: MutableStateFlow<NonEmptyStack<RootStackElementSkeleton>> =
            MutableStateFlow(NonEmptyStack(ElementSkeleton.action)),
    )

    @Pipe
    interface Dependencies {

        val preferences: Preferences

        fun action(
            config: StateFlow<Config>,
        ): ActionModel.Dependencies
    }

    @SealUp(
        variants = [
            Variant(
                type = ActionModel::class,
                identifier = "action",
            ),
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
                type = Unit::class,
                identifier = "action",
            ),
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

    private val config: Preference<Config> = dependencies
        .preferences["config"]
        .map(
            scope = scope,
            mapper = Json.toMapper(Config.serializer()),
        )
        .withDefault(scope) { Config.default }

    private val stackWithModels: StateFlow<NonEmptyStack<SkeletonWithModel<RootStackElementSkeleton, RootStackElementModel>>> =
        skeleton
            .stack
            .withModels(
                scope = scope,
                getKey = RootStackElementSkeleton::ordinal,
            ) { modelScope, skeleton ->
                createModel(
                    modelScope = modelScope,
                    elementSkeleton = skeleton,
                )
            }

    private fun createModel(
        modelScope: CoroutineScope,
        elementSkeleton: RootStackElementSkeleton,
    ): RootStackElementModel = elementSkeleton.fold(
        ifAction = {
            Element.action(
                scope = scope,
                dependencies = dependencies.action(
                    config = config.value,
                ),
                editConfig = {
                    skeleton
                        .stack
                        .push(
                            ElementSkeleton.form(
                                initial = config.value.value
                            )
                        )
                }
            )
        },
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