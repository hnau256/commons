package org.hnau.commons.app.test.app

import kotlinx.coroutines.CoroutineScope
import org.hnau.commons.app.model.app.AppModel
import org.hnau.commons.app.projector.app.AppProjector
import org.hnau.commons.app.test.data.Currency
import org.hnau.commons.app.test.model.RootModel
import org.hnau.commons.app.test.projector.Localization
import org.hnau.commons.app.test.projector.RootProjector
import org.hnau.commons.app.test.projector.impl

fun createAppProjector(
    scope: CoroutineScope,
    model: AppModel<RootModel, RootModel.Skeleton>,
): AppProjector<RootModel, RootModel.Skeleton, RootProjector> = AppProjector(
    scope = scope,
    model = model,
    createProjector = { scope, model ->
        RootProjector(
            scope = scope,
            model = model,
            dependencies = RootProjector.Dependencies.impl(
                localization = Localization.default,
                currency = Currency.default, //TODO
            ),
        )
    },
    content = { rootProjector, contentPadding ->
        rootProjector.Content(
            contentPadding = contentPadding,
        )
    }
)