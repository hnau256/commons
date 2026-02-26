package hnau.commons.kotlin.lazy

class AsyncLazy<T>(
  private val init: suspend () -> T,
) {

  private val initializer = AsyncInitializer<T>()

  suspend fun get(): T = initializer(init)
}
