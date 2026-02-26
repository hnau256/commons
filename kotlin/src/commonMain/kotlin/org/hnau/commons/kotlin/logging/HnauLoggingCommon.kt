package hnau.commons.kotlin.logging

import co.touchlab.kermit.LogWriter
import co.touchlab.kermit.Logger

expect fun getPlatformLogWriters(): List<LogWriter>

fun initHnauLogging() {
    Logger.setLogWriters(getPlatformLogWriters())
}
