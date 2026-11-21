package org.hnau.commons.app.test.android

import kotlinx.io.files.Path
import org.hnau.commons.app.model.app.RootModelFactory
import org.hnau.commons.app.model.file.File
import org.hnau.commons.app.projector.app.AppActivity
import org.hnau.commons.app.projector.app.RootProjectorFactory
import org.hnau.commons.app.test.app.TestRootProjectorFactory
import org.hnau.commons.app.test.app.createTestRootModelFactory
import org.hnau.commons.app.test.app.model.RootModel
import org.hnau.commons.app.test.app.projector.RootProjector

class TestActivity : AppActivity<RootModel.Skeleton, RootModel, RootProjector>() {

    override fun createRootModelFactory(): RootModelFactory<RootModel.Skeleton, RootModel> = createTestRootModelFactory(
        appFilesDir = filesDir
            .absolutePath
            .let(::Path)
            .let(::File)
    )

    override fun createRootProjectorFactory(): RootProjectorFactory<RootModel, RootProjector> =
        TestRootProjectorFactory
}