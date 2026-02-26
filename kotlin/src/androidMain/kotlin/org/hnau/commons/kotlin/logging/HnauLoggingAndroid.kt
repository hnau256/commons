package hnau.commons.kotlin.logging

import co.touchlab.kermit.LogWriter
import co.touchlab.kermit.LogcatWriter

actual fun getPlatformLogWriters(): List<LogWriter> = listOf(LogcatWriter())
