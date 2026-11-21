package org.hnau.commons.app.projector.app

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.hnau.commons.app.model.app.RootModelFactory
import org.hnau.commons.app.model.app.RootModelWrapper
import org.hnau.commons.app.model.theme.palette.SystemPalettes
import kotlin.system.exitProcess

fun <S, M, P> launchDesktopApplication(
    title: String,
    windowWidth: Dp,
    windowHeight: Dp,
    rootModelFactory: RootModelFactory<S, M>,
    rootProjectorFactory: RootProjectorFactory<M, P>,
    icon: Painter? = null,
) {

    val scope = CoroutineScope(
        SupervisorJob() + Dispatchers.Main.immediate + CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
            exitProcess(1)
        }
    )

    val rootModelWrapper = RootModelWrapper(
        scope = scope,
        savedState = null,
        root = rootModelFactory,
    )

    val projector = RootProjectorWrapper(
        scope = scope,
        model = rootModelWrapper,
        root = rootProjectorFactory,
        createSystemPalettes = { SystemPalettes.None },
    )

    application {
        Window(
            onCloseRequest = { exitApplication() },
            title = title,
            state = rememberWindowState(
                width = windowWidth,
                height = windowHeight,
            ),
            icon = icon,
        ) {
            projector.Content(
                contentPadding = PaddingValues.Zero,
            )
        }
    }
}
