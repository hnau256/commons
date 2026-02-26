package hnau.commons.kotlin.lazy

import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import kotlinx.atomicfu.locks.SynchronizedObject
import kotlinx.atomicfu.locks.synchronized

class Initializer<T>: SynchronizedObject() {

  @PublishedApi
  internal var cachedValue: Option<T> = None

   inline operator fun invoke(
    init:  () -> T,
  ): T = when (val localCachedValue = cachedValue) {
    is Some<T> -> localCachedValue.value
    None -> synchronized(this) { getUnsafe(init) }
  }

  @PublishedApi
  internal  inline fun getUnsafe(
    init:  () -> T,
  ): T = when (val localCachedValue = cachedValue) {
    is Some<T> -> localCachedValue.value
    None -> {
      val result = init()
      cachedValue = Some(result)
      result
    }
  }
}
