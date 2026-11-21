package org.hnau.commons.app.test.app

import org.hnau.commons.app.model.app.RootModelFactory
import org.hnau.commons.app.model.file.File
import org.hnau.commons.app.model.file.plus
import org.hnau.commons.app.model.preferences.impl.FileBasedPreferences
import org.hnau.commons.app.test.app.model.RootModel
import org.hnau.commons.app.test.app.model.impl

fun createTestRootModelFactory(
    appFilesDir: File,
): RootModelFactory<RootModel.Skeleton, RootModel> = RootModelFactory(
    skeletonSerializer = RootModel.Skeleton.serializer(),
    createDefaultSkeleton = { RootModel.Skeleton() },
    createRootModel = { scope, skeleton ->
        RootModel(
            scope = scope,
            dependencies = RootModel.Dependencies.impl(
                preferencesFactory = FileBasedPreferences.Factory(
                    preferencesFile = appFilesDir + "preferences.txt",
                ),
            ),
            skeleton = skeleton,
        )
    },
    extractGoBackHandler = RootModel::goBackHandler,
)