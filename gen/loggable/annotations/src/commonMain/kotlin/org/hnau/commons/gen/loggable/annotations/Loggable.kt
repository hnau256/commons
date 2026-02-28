package org.hnau.commons.gen.loggable.annotations

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class Loggable(
    val tag: String = "",
)
