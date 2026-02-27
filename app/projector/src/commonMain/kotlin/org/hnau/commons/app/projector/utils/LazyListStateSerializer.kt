package org.hnau.commons.app.projector.utils

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.LazyListState
import kotlinx.serialization.Serializable
import org.hnau.commons.kotlin.mapper.Mapper
import org.hnau.commons.kotlin.serialization.MappingKSerializer

@OptIn(ExperimentalFoundationApi::class)
object LazyListStateSerializer :
    MappingKSerializer<LazyListStateSerializer.Surrogate, LazyListState>(
        base = Surrogate.serializer(),
        mapper =
            Mapper(
                direct = { surrogate ->
                    LazyListState(
                        firstVisibleItemIndex = surrogate.item,
                        firstVisibleItemScrollOffset = surrogate.offset,
                    )
                },
                reverse = { lazyListState ->
                    Surrogate(
                        item = lazyListState.firstVisibleItemIndex,
                        offset = lazyListState.firstVisibleItemScrollOffset,
                    )
                },
            ),
    ) {
    @Serializable
    data class Surrogate(
        val item: Int,
        val offset: Int,
    )
}
