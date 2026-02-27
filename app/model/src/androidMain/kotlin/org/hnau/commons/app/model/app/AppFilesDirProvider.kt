package org.hnau.commons.app.model.app

import android.content.Context
import org.hnau.commons.app.model.file.File
import kotlinx.io.files.Path

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class AppFilesDirProvider(
    private val context: Context,
) {

    actual fun getAppFilesDir(): File = context
        .filesDir
        .absolutePath
        .let(::Path)
        .let(::File)
}