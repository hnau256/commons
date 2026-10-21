package org.hnau.commons.kotlin.logging.slf4j

import org.slf4j.ILoggerFactory
import org.slf4j.Logger
import java.util.concurrent.ConcurrentHashMap

/**
 * SLF4J ILoggerFactory implementation that creates Kermit-backed loggers
 */
internal class KermitLoggerFactory : ILoggerFactory {
    private val loggers = ConcurrentHashMap<String, Logger>()

    override fun getLogger(name: String): Logger =
        loggers.computeIfAbsent(name) {
            KermitSlf4jLogger(name)
        }
}
