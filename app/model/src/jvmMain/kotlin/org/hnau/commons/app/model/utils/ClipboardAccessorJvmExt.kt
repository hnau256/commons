package org.hnau.commons.app.model.utils

import java.awt.Toolkit
import java.awt.datatransfer.Clipboard
import java.awt.datatransfer.StringSelection


fun ClipboardAccessor.Companion.createForJvm(): ClipboardAccessor = object : ClipboardAccessor {

    override fun copyToClipboard(
        text: String,
    ): Result<Unit> = runCatching {
        Toolkit
            .getDefaultToolkit()
            .systemClipboard
            .setContents(
                StringSelection(text),
                null,
            )
    }
}