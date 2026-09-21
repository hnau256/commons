package org.hnau.commons.app.model.app

import kotlinx.io.files.Path
import org.hnau.commons.app.model.file.File

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class AppFilesDirProvider {

    actual fun getAppFilesDir(): File = File(Path(""))
}