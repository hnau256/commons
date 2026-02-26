package org.hnau.commons.kotlin.logging

import co.touchlab.kermit.LogWriter
import co.touchlab.kermit.Severity
import org.slf4j.LoggerFactory

class Slf4jLogWriter : LogWriter() {
    override fun log(
        severity: Severity,
        message: String,
        tag: String,
        throwable: Throwable?,
    ) {
        val logger = LoggerFactory.getLogger(tag)
        when (severity) {
            Severity.Verbose, Severity.Debug -> logger.debug(message, throwable)
            Severity.Info -> logger.info(message, throwable)
            Severity.Warn -> logger.warn(message, throwable)
            Severity.Error, Severity.Assert -> logger.error(message, throwable)
        }
    }
}

actual fun getPlatformLogWriters(): List<LogWriter> = listOf(Slf4jLogWriter())
