package org.hnau.commons.app.test.app

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.InternalComposeApi
import androidx.compose.runtime.remember
import androidx.compose.ui.InternalComposeUiApi
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import co.touchlab.kermit.Logger
import co.touchlab.kermit.platformLogWriter
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.runBlocking
import kotlin.system.exitProcess
import org.hnau.commons.app.model.app.AppFilesDirProvider
import org.hnau.commons.app.model.app.DesktopApp
import org.hnau.commons.app.model.theme.palette.SystemPalettes

@OptIn(InternalComposeApi::class, InternalComposeUiApi::class)
fun main() = runBlocking {

    Logger.setLogWriters(platformLogWriter())

    val scope = CoroutineScope(
        coroutineContext + SupervisorJob() + CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
            exitProcess(1)
        }
    )

    val app = DesktopApp(
        scope = scope,
        seed = createCommonsAppTestAppSeed(
            appFilesDirProvider = AppFilesDirProvider()
        ),
    )
    val projector = createAppProjector(
        scope = scope,
        model = app,
        systemPalettes = { SystemPalettes.None },
    )
    application {
        val scale = 2f
        Window(
            onCloseRequest = { exitApplication() },
            title = "CommonsAppTest",
            state = rememberWindowState(
                width = 256.dp * scale,
                height = 400.dp * scale,
            ),
            //icon = rememberVectorPainter(commonsAppTestIcon.s256),
        ) {
            val density = remember(scale) { Density(scale) }
            CompositionLocalProvider(
                LocalDensity provides density,
            ) {
                projector.Content(
                    contentPadding = PaddingValues.Zero,
                )
            }
        }
    }
}