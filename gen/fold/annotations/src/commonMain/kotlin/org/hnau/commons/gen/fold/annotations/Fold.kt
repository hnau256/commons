package org.hnau.commons.gen.fold.annotations

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class Fold(
    val nonEmptyEntries: Boolean = false,
    val dispatchTypeParameter: String = "",
)
