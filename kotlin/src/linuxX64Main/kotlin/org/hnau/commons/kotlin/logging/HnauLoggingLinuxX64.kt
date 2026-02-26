package hnau.commons.kotlin.logging

import co.touchlab.kermit.LogWriter
import co.touchlab.kermit.Severity

class StdoutLogWriter : LogWriter() {
    override fun log(
        severity: Severity,
        message: String,
        tag: String,
        throwable: Throwable?,
    ) {
        val output =
            if (throwable != null) {
                "$message\n$throwable"
            } else {
                message
            }
        println("[$severity] $tag: $output")
    }
}

actual fun getPlatformLogWriters(): List<LogWriter> = listOf(StdoutLogWriter())
