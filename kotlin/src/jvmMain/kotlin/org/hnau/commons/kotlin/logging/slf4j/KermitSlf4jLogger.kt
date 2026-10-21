package org.hnau.commons.kotlin.logging.slf4j

import org.slf4j.Logger
import org.slf4j.Marker
import co.touchlab.kermit.Logger as KermitLogger

/**
 * SLF4J Logger implementation that delegates to Kermit
 */
internal class KermitSlf4jLogger(
    private val name: String,
) : Logger {
    private val logger = KermitLogger.withTag(name)

    override fun getName(): String = name

    override fun isTraceEnabled(): Boolean = true

    override fun isTraceEnabled(marker: Marker?): Boolean = true

    override fun isDebugEnabled(): Boolean = true

    override fun isDebugEnabled(marker: Marker?): Boolean = true

    override fun isInfoEnabled(): Boolean = true

    override fun isInfoEnabled(marker: Marker?): Boolean = true

    override fun isWarnEnabled(): Boolean = true

    override fun isWarnEnabled(marker: Marker?): Boolean = true

    override fun isErrorEnabled(): Boolean = true

    override fun isErrorEnabled(marker: Marker?): Boolean = true

    override fun trace(msg: String) = logger.v { msg }

    override fun trace(
        format: String,
        arg: Any?,
    ) = logger.v { format.format(arg) }

    override fun trace(
        format: String,
        arg1: Any?,
        arg2: Any?,
    ) = logger.v { format.format(arg1, arg2) }

    override fun trace(
        format: String,
        vararg arguments: Any?,
    ) = logger.v { format.format(*arguments) }

    override fun trace(
        msg: String,
        t: Throwable?,
    ) = logger.v(t) { msg }

    override fun trace(
        marker: Marker?,
        msg: String,
    ) = trace(msg)

    override fun trace(
        marker: Marker?,
        format: String,
        arg: Any?,
    ) = trace(format, arg)

    override fun trace(
        marker: Marker?,
        format: String,
        arg1: Any?,
        arg2: Any?,
    ) = trace(format, arg1, arg2)

    override fun trace(
        marker: Marker?,
        format: String,
        vararg argArray: Any?,
    ) = trace(format, *argArray)

    override fun trace(
        marker: Marker?,
        msg: String,
        t: Throwable?,
    ) = trace(msg, t)

    override fun debug(msg: String) = logger.d { msg }

    override fun debug(
        format: String,
        arg: Any?,
    ) = logger.d { format.format(arg) }

    override fun debug(
        format: String,
        arg1: Any?,
        arg2: Any?,
    ) = logger.d { format.format(arg1, arg2) }

    override fun debug(
        format: String,
        vararg arguments: Any?,
    ) = logger.d { format.format(*arguments) }

    override fun debug(
        msg: String,
        t: Throwable?,
    ) = logger.d(t) { msg }

    override fun debug(
        marker: Marker?,
        msg: String,
    ) = debug(msg)

    override fun debug(
        marker: Marker?,
        format: String,
        arg: Any?,
    ) = debug(format, arg)

    override fun debug(
        marker: Marker?,
        format: String,
        arg1: Any?,
        arg2: Any?,
    ) = debug(format, arg1, arg2)

    override fun debug(
        marker: Marker?,
        format: String,
        vararg argArray: Any?,
    ) = debug(format, *argArray)

    override fun debug(
        marker: Marker?,
        msg: String,
        t: Throwable?,
    ) = debug(msg, t)

    override fun info(msg: String) = logger.i { msg }

    override fun info(
        format: String,
        arg: Any?,
    ) = logger.i { format.format(arg) }

    override fun info(
        format: String,
        arg1: Any?,
        arg2: Any?,
    ) = logger.i { format.format(arg1, arg2) }

    override fun info(
        format: String,
        vararg arguments: Any?,
    ) = logger.i { format.format(*arguments) }

    override fun info(
        msg: String,
        t: Throwable?,
    ) = logger.i(t) { msg }

    override fun info(
        marker: Marker?,
        msg: String,
    ) = info(msg)

    override fun info(
        marker: Marker?,
        format: String,
        arg: Any?,
    ) = info(format, arg)

    override fun info(
        marker: Marker?,
        format: String,
        arg1: Any?,
        arg2: Any?,
    ) = info(format, arg1, arg2)

    override fun info(
        marker: Marker?,
        format: String,
        vararg argArray: Any?,
    ) = info(format, *argArray)

    override fun info(
        marker: Marker?,
        msg: String,
        t: Throwable?,
    ) = info(msg, t)

    override fun warn(msg: String) = logger.w { msg }

    override fun warn(
        format: String,
        arg: Any?,
    ) = logger.w { format.format(arg) }

    override fun warn(
        format: String,
        arg1: Any?,
        arg2: Any?,
    ) = logger.w { format.format(arg1, arg2) }

    override fun warn(
        format: String,
        vararg arguments: Any?,
    ) = logger.w { format.format(*arguments) }

    override fun warn(
        msg: String,
        t: Throwable?,
    ) = logger.w(t) { msg }

    override fun warn(
        marker: Marker?,
        msg: String,
    ) = warn(msg)

    override fun warn(
        marker: Marker?,
        format: String,
        arg: Any?,
    ) = warn(format, arg)

    override fun warn(
        marker: Marker?,
        format: String,
        arg1: Any?,
        arg2: Any?,
    ) = warn(format, arg1, arg2)

    override fun warn(
        marker: Marker?,
        format: String,
        vararg argArray: Any?,
    ) = warn(format, *argArray)

    override fun warn(
        marker: Marker?,
        msg: String,
        t: Throwable?,
    ) = warn(msg, t)

    override fun error(msg: String) = logger.e { msg }

    override fun error(
        format: String,
        arg: Any?,
    ) = logger.e { format.format(arg) }

    override fun error(
        format: String,
        arg1: Any?,
        arg2: Any?,
    ) = logger.e { format.format(arg1, arg2) }

    override fun error(
        format: String,
        vararg arguments: Any?,
    ) = logger.e { format.format(*arguments) }

    override fun error(
        msg: String,
        t: Throwable?,
    ) = logger.e(t) { msg }

    override fun error(
        marker: Marker?,
        msg: String,
    ) = error(msg)

    override fun error(
        marker: Marker?,
        format: String,
        arg: Any?,
    ) = error(format, arg)

    override fun error(
        marker: Marker?,
        format: String,
        arg1: Any?,
        arg2: Any?,
    ) = error(format, arg1, arg2)

    override fun error(
        marker: Marker?,
        format: String,
        vararg argArray: Any?,
    ) = error(format, *argArray)

    override fun error(
        marker: Marker?,
        msg: String,
        t: Throwable?,
    ) = error(msg, t)
}
