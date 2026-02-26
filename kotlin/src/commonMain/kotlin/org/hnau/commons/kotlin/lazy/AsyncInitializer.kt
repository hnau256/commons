package org.hnau.commons.kotlin.lazy

import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class AsyncInitializer<T> {

  @PublishedApi
  internal val accessCacheMutex: Mutex = Mutex()

  @PublishedApi
  internal var cachedValue: Option<T> = None

  suspend inline operator fun invoke(
    init: suspend () -> T,
  ): T = when (val localCachedValue = cachedValue) {
    is Some<T> -> localCachedValue.value
    None -> accessCacheMutex.withLock { getUnsafe(init) }
  }

  @PublishedApi
  internal suspend inline fun getUnsafe(
    init: suspend () -> T,
  ): T = when (val localCachedValue = cachedValue) {
    is Some<T> -> localCachedValue.value
    None -> {
      val result = init()
      cachedValue = Some(result)
      result
    }
  }
}
