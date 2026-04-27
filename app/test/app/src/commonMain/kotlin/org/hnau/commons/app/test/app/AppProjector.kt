package org.hnau.commons.app.test.app

import kotlinx.coroutines.CoroutineScope
import org.hnau.commons.app.model.app.AppModel
import org.hnau.commons.app.model.theme.ThemeBrightness
import org.hnau.commons.app.model.theme.color.Hue
import org.hnau.commons.app.model.theme.palette.SystemPalettes
import org.hnau.commons.app.projector.app.AppProjector
import org.hnau.commons.app.test.app.model.RootModel
import org.hnau.commons.app.test.app.projector.RootProjector
import org.hnau.commons.app.test.app.projector.impl

fun createAppProjector(
    scope: CoroutineScope,
    systemPalettes: (ThemeBrightness) -> SystemPalettes,
    model: AppModel<RootModel, RootModel.Skeleton>,
): AppProjector<RootModel, RootModel.Skeleton, RootProjector> = AppProjector(
    scope = scope,
    model = model,
    systemPalettes = systemPalettes,
    fallbackHue = Hue(270),
    createProjector = { scope, model ->
        RootProjector(
            scope = scope,
            model = model,
            dependencies = RootProjector.Dependencies.impl(),
        )
    },
    content = { rootProjector, contentPadding ->
        rootProjector.Content(
            contentPadding = contentPadding,
        )
    }
)