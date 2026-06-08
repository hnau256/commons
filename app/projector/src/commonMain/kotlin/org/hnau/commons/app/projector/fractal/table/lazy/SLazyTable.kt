package org.hnau.commons.app.projector.fractal.table.lazy

import androidx.compose.foundation.OverscrollEffect
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberOverscrollEffect
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.hnau.commons.app.projector.fractal.distance.LocalDistance
import org.hnau.commons.app.projector.fractal.size.units
import org.hnau.commons.app.projector.fractal.utils.LocalShapeCorners
import org.hnau.commons.app.projector.fractal.utils.ShapeCorners
import org.hnau.commons.app.projector.utils.Orientation
import org.hnau.commons.app.projector.utils.fold
import org.hnau.commons.kotlin.foldBoolean

@Composable
fun SLazyTable(
    orientation: Orientation,
    modifier: Modifier = Modifier,
    corners: ShapeCorners.Provider = LocalShapeCorners.current,
    reverseOrdering: Boolean = false,
    state: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    flingBehavior: FlingBehavior = ScrollableDefaults.flingBehavior(),
    userScrollEnabled: Boolean = true,
    overscrollEffect: OverscrollEffect? = rememberOverscrollEffect(),
    content: SLazyTableScope.() -> Unit,
) {

    val arrangement = Arrangement.spacedBy(LocalDistance.current.units.borderWidth)

    val lazyListContent: LazyListScope.() -> Unit = {
        val scope: SLazyTableScope = SLazyTableScopeImpl(
            lazyListScope = this,
            orientation = orientation,
            corners = corners,
            reverseOrdering = reverseOrdering,
        )
        scope.content()
    }

    orientation.fold(
        ifHorizontal = {
            LazyRow(
                modifier = modifier,
                state = state,
                contentPadding = contentPadding,
                reverseLayout = reverseOrdering,
                horizontalArrangement = arrangement,
                flingBehavior = flingBehavior,
                userScrollEnabled = userScrollEnabled,
                overscrollEffect = overscrollEffect,
                content = lazyListContent,
            )
        },
        ifVertical = {
            LazyColumn(
                modifier = modifier,
                state = state,
                contentPadding = contentPadding,
                reverseLayout = reverseOrdering,
                verticalArrangement = arrangement,
                flingBehavior = flingBehavior,
                userScrollEnabled = userScrollEnabled,
                overscrollEffect = overscrollEffect,
                content = lazyListContent,
            )
        }
    )
}

interface SLazyTableScope : LazyListScope {

    val orientation: Orientation

    fun cells(
        count: Int,
        key: ((index: Int) -> Any)? = null,
        contentType: (index: Int) -> Any? = { null },
        cellContent: @Composable LazyItemScope.(index: Int) -> Unit,
    )

    companion object
}

fun <T> SLazyTableScope.cells(
    items: List<T>,
    key: ((T) -> Any)? = null,
    contentType: (T) -> Any? = { null },
    cellContent: @Composable (LazyItemScope.(T) -> Unit)
) {
    cells(
        count = items.size,
        key = key?.let { keyNotNull ->
            { index -> items[index].let(keyNotNull) }
        },
        contentType = { index -> items[index].let(contentType) },
        cellContent = { index -> cellContent(items[index]) },
    )
}

fun SLazyTableScope.cell(
    cellContent: @Composable (LazyItemScope.() -> Unit)
) {
    cells(
        count = 1,
        cellContent = { cellContent() },
    )
}

private class SLazyTableScopeImpl(
    private val lazyListScope: LazyListScope,
    override val orientation: Orientation,
    private val corners: ShapeCorners.Provider,
    private val reverseOrdering: Boolean,
) : LazyListScope by lazyListScope, SLazyTableScope {

    private var isFirstCells = true

    override fun cells(
        count: Int,
        key: ((index: Int) -> Any)?,
        contentType: (index: Int) -> Any?,
        cellContent: @Composable (LazyItemScope.(index: Int) -> Unit)
    ) {

        val isFirstCells = this@SLazyTableScopeImpl.isFirstCells
        this@SLazyTableScopeImpl.isFirstCells = false

        val corners = this@SLazyTableScopeImpl.corners
        val orientation = this@SLazyTableScopeImpl.orientation
        val reverseOrdering = this@SLazyTableScopeImpl.reverseOrdering

        items(
            count = count,
            key = key,
            contentType = contentType,
        ) { index ->
            val isFirst = index == 0
            val isLast = index == count - 1
            val cornersProvider = remember(
                isFirst,
                isLast,
                corners,
            ) {
                val corners = corners
                    .getTableCorners()
                    .close(
                        orientation = orientation,
                        startOrTop = !isFirst,
                        endOrBottom = !isLast,
                    )
                ShapeCorners.Provider { corners }
            }
            CompositionLocalProvider(
                value = LocalShapeCorners provides cornersProvider,
            ) {
                val separation = LocalDistance.current.units.padding.along.medium
                Box(
                    modifier = remember(
                        separation,
                        orientation,
                        reverseOrdering,
                        isFirstCells,
                        ) {
                        when {
                            isFirst && !isFirstCells -> reverseOrdering.foldBoolean(
                                ifFalse = {
                                    orientation.fold(
                                        ifHorizontal = { Modifier.padding(start = separation) },
                                        ifVertical = { Modifier.padding(top = separation) },
                                    )
                                },
                                ifTrue = {
                                    orientation.fold(
                                        ifHorizontal = { Modifier.padding(end = separation) },
                                        ifVertical = { Modifier.padding(bottom = separation) },
                                    )
                                }
                            )

                            else -> Modifier
                        }
                    },
                    propagateMinConstraints = true,
                ) {
                    cellContent(index)
                }

            }
        }
    }
}