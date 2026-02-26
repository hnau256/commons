package org.hnau.commons.kotlin


inline fun <I, O> Triple<I, I, I>.map(
    transform: (I) -> O,
): Triple<O, O, O> = Triple(
    first = transform(first),
    second = transform(second),
    third = transform(third),
)

inline fun <FI, S, T, FO> Triple<FI, S, T>.mapFirst(
    transform: (FI) -> FO,
): Triple<FO, S, T> = Triple(
    first = transform(first),
    second = second,
    third = third,
)

inline fun <F, SI, T, SO> Triple<F, SI, T>.mapSecond(
    transform: (SI) -> SO,
): Triple<F, SO, T> = Triple(
    first = first,
    second = transform(second),
    third = third,
)

inline fun <F, S, TI, TO> Triple<F, S, TI>.mapThird(
    transform: (TI) -> TO,
): Triple<F, S, TO> = Triple(
    first = first,
    second = second,
    third = transform(third),
)