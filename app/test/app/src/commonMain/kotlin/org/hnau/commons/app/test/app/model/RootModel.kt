package org.hnau.commons.app.test.app.model

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.Serializable
import org.hnau.commons.app.model.goback.GoBackHandler
import org.hnau.commons.app.model.goback.NeverGoBackHandler
import org.hnau.commons.app.model.preferences.Preferences
import org.hnau.commons.gen.pipe.annotations.Pipe
import org.hnau.commons.kotlin.Loadable
import org.hnau.commons.kotlin.LoadableStateFlow
import org.hnau.commons.kotlin.coroutines.flow.state.flatMapState
import org.hnau.commons.kotlin.coroutines.flow.state.mapWithScope
import org.hnau.commons.kotlin.fold
import org.hnau.commons.kotlin.map

class RootModel(
    scope: CoroutineScope,
    dependencies: Dependencies,
    skeleton: Skeleton,
) {

    @Pipe
    interface Dependencies {

        val preferencesFactory: Preferences.Factory

        fun stack(
            preferences: Preferences,
        ): RootStackModel.Dependencies

        companion object
    }

    @Serializable
    data class Skeleton(
        val stack: RootStackModel.Skeleton = RootStackModel.Skeleton(),
    )

    val stack: StateFlow<Loadable<RootStackModel>> = LoadableStateFlow(scope) {
        dependencies
            .preferencesFactory
            .createPreferences(scope)
    }.mapWithScope(scope) { scope, preferencesOrLoading ->
        preferencesOrLoading.map { preferences ->
            RootStackModel(
                scope = scope,
                skeleton = skeleton.stack,
                dependencies = dependencies.stack(preferences),
            )
        }

    }

    val goBackHandler: GoBackHandler = stack
        .flatMapState(scope) { currentMainModel ->
            currentMainModel.fold(
                ifLoading = { NeverGoBackHandler },
                ifReady = RootStackModel::goBackHandler,
            )
        }
}