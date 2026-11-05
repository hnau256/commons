package org.hnau.commons.app.model.app

import kotlinx.coroutines.CoroutineScope

fun <S, M> DesktopApp(
    scope: CoroutineScope,
    seed: AppSeed<S, M>,
): AppModel<S, M> = AppModel(
    scope = scope,
    savedState = SavedState(null),
    seed = seed,
)