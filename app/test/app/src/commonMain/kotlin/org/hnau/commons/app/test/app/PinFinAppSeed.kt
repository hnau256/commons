package org.hnau.commons.app.test.app

import org.hnau.commons.app.model.app.AppSeed
import org.hnau.commons.app.model.file.absolutePath
import org.hnau.commons.app.model.file.plus
import org.hnau.commons.app.model.preferences.impl.FileBasedPreferences
import org.hnau.commons.app.model.theme.ThemeBrightness
import org.hnau.commons.app.model.theme.Hue
import org.hnau.commons.app.test.model.RootModel
import org.hnau.commons.app.test.model.impl
import org.hnau.commons.app.test.model.utils.budget.storage.BudgetsStorage
import org.hnau.commons.app.test.model.utils.budget.storage.impl
import org.hnau.commons.app.test.model.utils.budget.storage.impl.files

fun createCommonsAppTestAppSeed(
    dependencies: CommonsAppTestAppDependencies,
    defaultBrightness: ThemeBrightness? = null,
): AppSeed<RootModel, RootModel.Skeleton> = AppSeed(
    fallbackHue = Hue(240),
    defaultBrightness = defaultBrightness,
    skeletonSerializer = RootModel.Skeleton.serializer(),
    createDefaultSkeleton = { RootModel.Skeleton() },
    createModel = { scope, appContext, skeleton ->
        RootModel(
            scope = scope,
            dependencies = RootModel.Dependencies.impl(
                preferencesFactory = FileBasedPreferences.Factory(
                    preferencesFile = appContext.filesDir + "preferences.txt",
                ),
                budgetsStorageFactory = BudgetsStorage.Factory.files(
                    budgetsDir = (appContext.filesDir + "budgets").absolutePath,
                    dependencies = BudgetsStorage.Factory.Dependencies.impl(
                        currency = dependencies.currency,
                    ),
                ),
                currency = dependencies.currency,
            ),
            skeleton = skeleton,
        )
    },
)