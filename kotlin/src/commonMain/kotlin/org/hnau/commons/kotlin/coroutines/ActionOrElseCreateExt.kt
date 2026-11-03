package org.hnau.commons.kotlin.coroutines

fun <I> ActionOrElse.Companion.instant(
    instantAction: (I) -> Unit,
): ActionOrElse.Action<I> = ActionOrElse.Action(
    action = instantAction,
)

fun ActionOrElse.Companion.instant(
    instantAction: () -> Unit,
): ActionOrElse.Action<Unit> =
    ActionOrElse.instant { _ -> instantAction() }

val ActionOrElse.Companion.noAction: ActionOrElse.Action<Unit>?
    get() = null