package org.hnau.commons.app.test.app

import androidx.compose.ui.unit.dp
import co.touchlab.kermit.Logger
import co.touchlab.kermit.platformLogWriter
import kotlinx.io.files.Path
import org.hnau.commons.app.model.file.File
import org.hnau.commons.app.projector.app.launchDesktopApplication

fun main() {
    Logger.setLogWriters(platformLogWriter())
    launchDesktopApplication(
        title = "CommonsAppTest",
        windowWidth = 512.dp,
        windowHeight = 640.dp,
        rootModelFactory = createTestRootModelFactory(
            appFilesDir = File(Path(""))
        ),
        rootProjectorFactory = TestRootProjectorFactory,
    )
}