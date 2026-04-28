package org.hnau.commons.app.test.app

import org.hnau.commons.app.model.app.AppSeed
import org.hnau.commons.app.test.app.model.RootModel
import org.hnau.commons.app.test.app.model.impl

fun createCommonsAppTestAppSeed(
    dependencies: CommonsAppTestAppDependencies,
): AppSeed<RootModel, RootModel.Skeleton> = AppSeed(
    skeletonSerializer = RootModel.Skeleton.serializer(),
    createDefaultSkeleton = { RootModel.Skeleton() },
    createModel = { scope, skeleton ->
        RootModel(
            scope = scope,
            dependencies = RootModel.Dependencies.impl(),
            skeleton = skeleton,
        )
    },
)