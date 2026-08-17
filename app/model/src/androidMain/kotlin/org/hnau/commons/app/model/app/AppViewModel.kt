package org.hnau.commons.app.model.app

import android.os.Bundle
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory

class AppViewModel<M, S>(
    private val state: SavedStateHandle,
    seed: AppSeed<M, S>,
) : ViewModel() {

    val appModel = AppModel(
        scope = viewModelScope,
        savedState = SavedState(
            state
                .get<Bundle>(StateKey)
                ?.getString(StateKey),
        ),
        seed = seed,
    )

    init {
        state.setSavedStateProvider(StateKey) {
            Bundle().apply { putString(StateKey, appModel.savableState.savedState) }
        }
    }

    companion object {

        private const val StateKey = "state"

        fun <M, S> factory(
            seed: AppSeed<M, S>,
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val savedStateHandle = createSavedStateHandle()
                AppViewModel(
                    state = savedStateHandle,
                    seed = seed,
                )
            }
        }
    }
}
