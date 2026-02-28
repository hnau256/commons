package org.hnau.commons.kotlin.logging

import co.touchlab.kermit.Logger
import kotlin.coroutines.cancellation.CancellationException


inline fun <R> Logger.tryOrLog(
    log: String,
    block: () -> R,
): Result<R> = try {
    val result = block()
    Result.success(result)
} catch (ex: CancellationException) {
    throw ex
} catch (th: Throwable) {
    w(th) { "Error while $log" }
    Result.failure(th)
}