package org.hnau.commons.app.projector.app

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.safeDrawing
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.hnau.commons.app.model.app.RootModelFactory
import org.hnau.commons.app.model.goback.GoBackHandler
import org.hnau.commons.app.model.theme.palette.SystemPalettes

abstract class AppActivity<S, M, P> : ComponentActivity() {

    abstract fun createRootModelFactory(): RootModelFactory<S, M>

    abstract fun createRootProjectorFactory(): RootProjectorFactory<M, P>

    private val viewModel: RootModelWrapperViewModel<S, M> by viewModels {
        viewModelFactory {
            initializer {
                RootModelWrapperViewModel(
                    savedStateHandle = createSavedStateHandle(),
                    rootModelFactory = createRootModelFactory(),
                )
            }
        }
    }

    private val goBackHandler: GoBackHandler
        get() = viewModel.rootModelWrapper.goBackHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        initOnBackPressedDispatcherCallback()
        val projector = RootProjectorWrapper(
            scope = lifecycleScope,
            model = viewModel.rootModelWrapper,
            root = createRootProjectorFactory(),
            createSystemPalettes = SystemPalettes.getForAndroid(
                context = this,
            )

        )
        setContent {
            val systemBarsPadding = WindowInsets.safeDrawing.asPaddingValues()
            projector.Content(
                contentPadding = systemBarsPadding,
            )
        }
    }

    @Suppress("OVERRIDE_DEPRECATION", "DEPRECATION")
    override fun onBackPressed() {
        if (useOnBackPressedDispatcher) {
            super.onBackPressed()
        }
        goBackHandler
            .value
            ?.invoke()
            ?: super.onBackPressed()
    }

    private fun initOnBackPressedDispatcherCallback() {
        if (!useOnBackPressedDispatcher) {
            return
        }
        val callback = object : OnBackPressedCallback(
            enabled = goBackHandler.value != null,
        ) {
            override fun handleOnBackPressed() {
                goBackHandler.value?.invoke()
            }
        }
        lifecycleScope.launch {
            goBackHandler
                .map { it != null }
                .distinctUntilChanged()
                .collect { goBackIsAvailable ->
                    callback.isEnabled = goBackIsAvailable
                }
        }
        onBackPressedDispatcher.addCallback(this, callback)
    }

    companion object {

        private val useOnBackPressedDispatcher: Boolean = Build.VERSION.SDK_INT >= 33
    }

}