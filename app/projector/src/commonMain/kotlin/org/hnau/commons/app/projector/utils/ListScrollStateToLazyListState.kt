package org.hnau.commons.app.projector.utils

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.hnau.commons.app.model.ListScrollState

@Composable
@OptIn(ExperimentalFoundationApi::class)
fun StateFlow<ListScrollState>.toLazyListState(onListScrollStateChanged: (ListScrollState) -> Unit): LazyListState {
    val state =
        remember(this) {
            val initial = value
            LazyListState(
                firstVisibleItemIndex = initial.firstVisibleItemIndex,
                firstVisibleItemScrollOffset = initial.firstVisibleItemScrollOffset,
            )
        }

    LaunchedEffect(this) {
        collect { initial ->
            state.scrollToItem(
                index = initial.firstVisibleItemIndex,
                scrollOffset = initial.firstVisibleItemScrollOffset,
            )
        }
    }

    LaunchedEffect(state) {
        snapshotFlow {
            ListScrollState(
                state.firstVisibleItemIndex,
                state.firstVisibleItemScrollOffset,
            )
        }.collect(onListScrollStateChanged)
    }
    return state
}

@Composable
fun MutableStateFlow<ListScrollState>.toLazyListState(): LazyListState = toLazyListState(::value::set)
