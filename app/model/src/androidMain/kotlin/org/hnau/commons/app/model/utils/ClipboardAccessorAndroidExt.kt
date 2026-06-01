package org.hnau.commons.app.model.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import arrow.core.raise.result
import org.hnau.commons.kotlin.castOrNull
import org.hnau.commons.kotlin.ifNull

fun ClipboardAccessor.Companion.createForAndroid(
    context: Context
): ClipboardAccessor = object : ClipboardAccessor {

    override fun copyToClipboard(
        text: String,
    ): Result<Unit> = result {
        context
            .getSystemService(Context.CLIPBOARD_SERVICE)
            ?.castOrNull<ClipboardManager>()
            .ifNull { raise(IllegalStateException("Clipboard service not found")) }
            .setPrimaryClip(
                ClipData.newPlainText(
                    text,
                    text
                )
            )
    }
}

