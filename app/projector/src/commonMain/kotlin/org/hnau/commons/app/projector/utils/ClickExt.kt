package org.hnau.commons.app.projector.utils

val (() -> Unit)?.orNoAction: () -> Unit
    get() = { this?.invoke() }