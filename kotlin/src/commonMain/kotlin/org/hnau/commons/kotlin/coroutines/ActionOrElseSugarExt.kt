package org.hnau.commons.kotlin.coroutines

import org.hnau.commons.kotlin.invoke


fun ActionOrElse.Else<CancelOrInProgress.Cancel>.cancel() {
    cancelOrInProgress.cancel()
}

fun ActionOrElse<Unit, CancelOrInProgress.Cancel>.actionOrCancel() {
    fold(
        ifAction = { action -> action() },
        ifElse = CancelOrInProgress.Cancel::cancel::invoke
    )
}