package org.hnau.commons.kotlin.coroutines


sealed interface CancelOrInProgress {

    data class Cancel(
        val cancel: () -> Unit,
    ) : CancelOrInProgress

    data object InProgress : CancelOrInProgress

}

sealed interface ActionOrElse<out I, out E : CancelOrInProgress> {

    data class Action<I>(
        val action: (I) -> Unit,
    ) : ActionOrElse<I, Nothing>

    data class Else<out E : CancelOrInProgress>(
        val cancelOrInProgress: E,
    ) : ActionOrElse<Nothing, E>

    companion object
}