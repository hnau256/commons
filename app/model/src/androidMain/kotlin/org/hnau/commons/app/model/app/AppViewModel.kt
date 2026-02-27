package org.hnau.commons.app.model.app

import android.content.Context
import android.os.Bundle
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

class AppViewModel<M, S>(
    context: Context,
    private val state: SavedStateHandle,
    defaultTryUseSystemHue: Boolean = true,
    seed: AppSeed<M, S>,
) : ViewModel() {

    private val scope: CoroutineScope =
        CoroutineScope(SupervisorJob())

    val appModel = AppModel(
        scope = scope,
        savedState = SavedState(
            state
                .get<Bundle>(StateKey)
                ?.getString(StateKey),
        ),
        appFilesDirProvider = AppFilesDirProvider(
            context = context,
        ),
        defaultTryUseSystemHue = defaultTryUseSystemHue,
        seed = seed,
    )

    init {
        state.setSavedStateProvider(StateKey) {
            Bundle().apply { putString(StateKey, appModel.savableState.savedState) }
        }
    }

    override fun onCleared() {
        super.onCleared()
        scope.cancel()
    }

    companion object {

        private const val StateKey = "state"

        fun <M, S> factory(
            context: Context,
            defaultTryUseSystemHue: Boolean = true,
            seed: AppSeed<M, S>,
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val savedStateHandle = createSavedStateHandle()
                AppViewModel(
                    context = context,
                    state = savedStateHandle,
                    defaultTryUseSystemHue = defaultTryUseSystemHue,
                    seed = seed,
                )
            }
        }
    }
}
