package hnau.commons.kotlin.logging

import co.touchlab.kermit.LogWriter
import co.touchlab.kermit.slf4j.Slf4jLogWriter

actual fun getPlatformLogWriters(): List<LogWriter> = listOf(Slf4jLogWriter())
