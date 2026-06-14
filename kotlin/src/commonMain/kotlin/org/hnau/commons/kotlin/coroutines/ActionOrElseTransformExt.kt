package org.hnau.commons.kotlin.coroutines

inline fun <I, E : CancelOrInProgress, O> ActionOrElse<I, E>.fold(
    ifAction: ((I) -> Unit) -> O,
    ifElse: (E) -> O,
): O = when (this) {
    is ActionOrElse.Action -> ifAction(action)
    is ActionOrElse.Else -> ifElse(cancelOrInProgress)
}

inline fun <I, E : CancelOrInProgress, O> ActionOrElse<I, E>.map(
    crossinline transform: (O) -> I,
): ActionOrElse<O, E> = fold(
    ifElse = { ActionOrElse.Else(it) },
    ifAction = { action ->
        ActionOrElse.Action { value ->
            val transformedValue = transform(value)
            action(transformedValue)
        }
    }
)