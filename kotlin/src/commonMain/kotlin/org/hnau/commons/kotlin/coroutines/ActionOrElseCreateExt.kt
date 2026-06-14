package org.hnau.commons.kotlin.coroutines

fun <I> ActionOrElse.Companion.instant(
    instantAction: (I) -> Unit,
): ActionOrElse<I, CancelOrInProgress.Cancel> = ActionOrElse.Action(
    action = instantAction,
)

fun ActionOrElse.Companion.instant(
    instantAction: (() -> Unit)?,
): ActionOrElse<Unit, CancelOrInProgress.Cancel>? = instantAction?.let { action ->
    ActionOrElse.instant { _ -> action() }
}

val ActionOrElse.Companion.noAction: ActionOrElse<Unit, CancelOrInProgress.Cancel>?
    get() = null