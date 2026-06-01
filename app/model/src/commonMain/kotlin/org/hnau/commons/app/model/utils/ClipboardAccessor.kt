package org.hnau.commons.app.model.utils

interface ClipboardAccessor {

    fun copyToClipboard(
        text: String,
    ): Result<Unit>

    companion object
}