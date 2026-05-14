package org.hnau.commons.app.test.app

import org.hnau.commons.app.model.app.AppFilesDirProvider
import org.hnau.commons.app.model.app.AppSeed
import org.hnau.commons.app.model.file.plus
import org.hnau.commons.app.model.preferences.impl.FileBasedPreferences
import org.hnau.commons.app.test.app.model.RootModel
import org.hnau.commons.app.test.app.model.impl

fun createCommonsAppTestAppSeed(
    appFilesDirProvider: AppFilesDirProvider,
): AppSeed<RootModel, RootModel.Skeleton> = AppSeed(
    skeletonSerializer = RootModel.Skeleton.serializer(),
    createDefaultSkeleton = { RootModel.Skeleton() },
    createModel = { scope, skeleton ->
        val appFilesDir = appFilesDirProvider.getAppFilesDir()
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
)