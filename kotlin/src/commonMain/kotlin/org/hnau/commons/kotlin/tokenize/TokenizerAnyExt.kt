package org.hnau.commons.kotlin.tokenize

import arrow.core.NonEmptyList

fun <I> Tokenizer.OptionFactory.Companion.any(
    firstVariant: I,
    vararg otherVariants: I,
): Tokenizer.OptionFactory<I, Unit> = any(
    variants = NonEmptyList(
        head = firstVariant,
        tail = otherVariants.toList(),
    )
)

fun <I> Tokenizer.OptionFactory.Companion.any(
    variants: NonEmptyList<I>,
): Tokenizer.OptionFactory<I, Unit> = Tokenizer.OptionFactory { firstItem ->
    when (firstItem in variants) {
        true -> Tokenizer.alreadyCompleted(Unit)

        false -> null
    }
}