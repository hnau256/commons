package org.hnau.commons.app.test.app

import org.hnau.commons.app.model.theme.color.Hue
import org.hnau.commons.app.projector.app.RootProjectorFactory
import org.hnau.commons.app.test.app.model.RootModel
import org.hnau.commons.app.test.app.projector.RootProjector
import org.hnau.commons.app.test.app.projector.impl

val TestRootProjectorFactory: RootProjectorFactory<RootModel, RootProjector> = RootProjectorFactory(
    fallbackHue = Hue(300),
    createRootProjector = { scope, model, appUIContext ->
        RootProjector(
            scope = scope,
            model = model,
            dependencies = RootProjector.Dependencies.impl(
                bubblesShower = appUIContext.bubblesShower,
            ),
        )
    },
    content = { rootProjector, contentPadding ->
        rootProjector.Content(
            contentPadding = contentPadding,
        )
    }
)