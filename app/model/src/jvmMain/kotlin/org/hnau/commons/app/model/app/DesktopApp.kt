package org.hnau.commons.app.model.app

import kotlinx.coroutines.CoroutineScope

fun <M, S> DesktopApp(
    scope: CoroutineScope,
    seed: AppSeed<M, S>,
): AppModel<M, S> = AppModel(
    scope = scope,
    savedState = SavedState(null),
    seed = seed,
)