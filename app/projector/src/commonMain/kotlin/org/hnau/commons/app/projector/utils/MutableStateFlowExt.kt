package org.hnau.commons.app.projector.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import org.hnau.commons.kotlin.MutableAccessor
import org.hnau.commons.kotlin.mapper.Mapper
import org.hnau.commons.kotlin.mapper.equality
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
fun <I, O> MutableStateFlow<I>.collectAsMutableAccessor(
    mapper: Mapper<I, O>,
): MutableAccessor<O> {

    val source: MutableStateFlow<I> = this

    var cache: O by remember { mutableStateOf(source.value.let(mapper.direct)) }

    LaunchedEffect(source) {
        source.collect { newValue ->
            cache = newValue.let(mapper.direct)
        }
    }

    return remember(source) {
        MutableAccessor(
            get = { cache },
            set = { newValue ->
                cache = newValue
                source.value = newValue.let(mapper.reverse)
            }
        )
    }
}

@Composable
fun <T> MutableStateFlow<T>.collectAsMutableAccessor(): MutableAccessor<T> =
    collectAsMutableAccessor(
        mapper = remember { Mapper.equality() },
    )