package org.hnau.commons.app.model

fun String.toEditingString(): EditingString =
    EditingString(text = this)
