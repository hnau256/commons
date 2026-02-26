package org.hnau.commons.kotlin.coroutines.flow.state

import org.hnau.commons.kotlin.coroutines.flow.toFakeStateFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalForInheritanceCoroutinesApi
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

fun <I, O> StateFlow<I>.mapState(
  scope: CoroutineScope,
  transform: (I) -> O,
): StateFlow<O> {

  val initialValue: I = this@mapState.value

  val onceCache: OnceCache<I, Unit> =
      OnceCache(initialValue, Unit)

  val result: MutableStateFlow<O> = MutableStateFlow(transform(initialValue))

  scope.launch {
    this@mapState.collect { newValue: I ->

      val isValueSameAsInitial: Boolean =
        onceCache.popValueIfKeyIsSameAsInitial(newValue) != null

      if (isValueSameAsInitial) {
        return@collect
      }

      result.value = transform(newValue)
    }
  }

  return result
}

fun <I, O> StateFlow<I>.mapWithScope(
    scope: CoroutineScope,
    transform: (CoroutineScope, I) -> O,
): StateFlow<O> = this
    .scopedInState(scope)
    .mapState(scope) { (scope, value) ->
        transform(scope, value)
    }

@Suppress("DEPRECATION")
@Deprecated("Result of this function is fake StateFlow. Transform will be called for all collectors")
fun <I, O> StateFlow<I>.mapStateLite(
    transform: (I) -> O,
): StateFlow<O> = map(transform).toFakeStateFlow { transform(value) }