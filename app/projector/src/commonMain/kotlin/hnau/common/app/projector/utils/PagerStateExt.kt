package hnau.common.app.projector.utils

import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import hnau.common.kotlin.coroutines.flow.state.mutable.mapMutableState
import hnau.common.kotlin.mapper.Mapper
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch


@Composable
fun rememberPagerState(
    state: MutableStateFlow<Int>,
    pageCount: () -> Int,
): PagerState {
    @Suppress("StateFlowValueCalledInComposition")
    val result = rememberPagerState(
        initialPage = state.value,
        pageCount = pageCount,
    )

    LaunchedEffect(state) {
        state.collect { page ->
            launch {
                result.animateScrollToPage(page)
            }
        }
    }

    val currentPage = result.currentPage
    LaunchedEffect(currentPage) {
        state.value = currentPage
    }

    return result
}

@Composable
fun <T> rememberPagerState(
    state: MutableStateFlow<T>,
    items: ImmutableList<T>,
): PagerState = rememberPagerState(
    state = rememberCoroutineScope().let { scope ->
        remember(state, scope) {
            val indexes = items.withIndex().associate { (i, value) -> value to i }
            state.mapMutableState(
                scope = scope, mapper = Mapper(
                    direct = indexes::getValue,
                    reverse = items::get,
                )
            )
        }
    },
    pageCount = { items.size },
)

@Composable
inline fun <reified T : Enum<T>> rememberPagerState(
    state: MutableStateFlow<T>,
): PagerState = rememberPagerState(
    state = state,
    items = remember { enumValues<T>().toImmutableList() },
)