package org.hnau.commons.app.model.utils

import arrow.atomic.AtomicBoolean
import arrow.core.Option
import arrow.core.Some
import arrow.core.raise.Raise
import arrow.core.raise.SingletonRaise
import arrow.core.raise.recover
import org.hnau.commons.kotlin.ifTrue
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

class EditableRaise @PublishedApi internal constructor(
    private val changedState: AtomicBoolean,
    private val raise: Raise<Editable.Incorrect>,
) : Raise<Editable.Incorrect> by raise {

    fun raise(): Nothing = raise(Editable.Incorrect)

    fun ensure(condition: Boolean) {
        contract { returns() implies condition }
        if (!condition) raise()
    }

    fun <A> Option<A>.bind(): A {
        contract { returns() implies (this@bind is Some) }
        return fold(
            ifEmpty = { raise() },
            ifSome = { it },
        )
    }

    fun <A> A?.bind(): A {
        contract { returns() implies (this@bind != null) }
        return this ?: raise()
    }

    fun <A> Editable<A>.bind(): A {
        contract { returns() implies (this@bind is Editable.Value) }
        return fold(
            ifIncorrect = { raise() },
            ifValue = { value, changed ->
                changed.ifTrue { changedState.value = true }
                value
            },
        )
    }
}

inline fun <A> editable(
    block: EditableRaise.() -> A,
): Editable<A> {
    contract { callsInPlace(block, InvocationKind.AT_MOST_ONCE) }
    return recover(
        block = {
            val changedState = AtomicBoolean(false)
            val raise = EditableRaise(changedState, this)
            val result = raise.block()
            Editable.Value(
                value = result,
                changed = changedState.get(),
            )
        },
        recover = { Editable.Incorrect },
    )
}

context(raise: SingletonRaise<E>)
fun <E, A> Editable<A>.bind(): A {
    contract { returns() implies (this@bind is Editable.Value) }
    return fold(
        ifIncorrect = { raise.raise() },
        ifValue = { value, _ -> value },
    )
}