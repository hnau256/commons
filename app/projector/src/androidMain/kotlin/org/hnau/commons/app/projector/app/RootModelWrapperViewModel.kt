package org.hnau.commons.app.projector.app

import android.os.Bundle
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import org.hnau.commons.app.model.app.RootModelFactory
import org.hnau.commons.app.model.app.RootModelWrapper

internal class RootModelWrapperViewModel<S, M>(
    savedStateHandle: SavedStateHandle,
    rootModelFactory: RootModelFactory<S, M>,
) : ViewModel() {

    val rootModelWrapper: RootModelWrapper<S, M> = RootModelWrapper(
        scope = viewModelScope,
        savedState = savedStateHandle
            .get<Bundle>(StateKey)
            ?.getString(StateKey),
        root = rootModelFactory,
    )

    init {
        savedStateHandle.setSavedStateProvider(StateKey) {
            Bundle().apply { putString(StateKey, rootModelWrapper.savableState) }
        }
    }

    companion object {

        private const val StateKey = "state"
    }
}
