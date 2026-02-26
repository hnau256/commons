package hnau.commons.kotlin

inline fun <I, O> Pair<I, I>.map(
    transform: (I) -> O,
): Pair<O, O> = Pair(
    first = transform(first),
    second = transform(second),
)

inline fun <FI, S, FO> Pair<FI, S>.mapFirst(
    transform: (FI) -> FO,
): Pair<FO, S> = Pair(
    first = transform(first),
    second = second,
)

inline fun <F, SI, SO> Pair<F, SI>.mapSecond(
    transform: (SI) -> SO,
): Pair<F, SO> = Pair(
    first = first,
    second = transform(second),
)