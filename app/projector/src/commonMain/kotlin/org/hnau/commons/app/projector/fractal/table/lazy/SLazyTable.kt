package org.hnau.commons.app.projector.fractal.table.lazy

import androidx.compose.foundation.OverscrollEffect
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberOverscrollEffect
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.hnau.commons.app.projector.fractal.distance.LocalDistance
import org.hnau.commons.app.projector.fractal.padding.LocalContentPadding
import org.hnau.commons.app.projector.fractal.size.units
import org.hnau.commons.app.projector.fractal.table.STable
import org.hnau.commons.app.projector.fractal.table.STableScope
import org.hnau.commons.app.projector.fractal.utils.LocalShapeCorners
import org.hnau.commons.app.projector.fractal.utils.ShapeCorners
import org.hnau.commons.app.projector.utils.Orientation
import org.hnau.commons.app.projector.utils.PaddingValues
import org.hnau.commons.app.projector.utils.acrossFrom
import org.hnau.commons.app.projector.utils.acrossTo
import org.hnau.commons.app.projector.utils.alongFrom
import org.hnau.commons.app.projector.utils.alongTo
import org.hnau.commons.app.projector.utils.copy
import org.hnau.commons.app.projector.utils.fold
import org.hnau.commons.app.projector.utils.opposite
import org.hnau.commons.app.projector.utils.plus

@Composable
fun SLazyTable(
    orientation: Orientation,
    modifier: Modifier = Modifier,
    corners: ShapeCorners.Provider = LocalShapeCorners.current,
    reverseOrdering: Boolean = false,
    state: LazyListState = rememberLazyListState(),
    flingBehavior: FlingBehavior = ScrollableDefaults.flingBehavior(),
    userScrollEnabled: Boolean = true,
    overscrollEffect: OverscrollEffect? = rememberOverscrollEffect(),
    content: SLazyTableScope.() -> Unit,
) {

    with(orientation) {

        val arrangement = Arrangement.spacedBy(LocalDistance.current.units.borderWidth)

        val contentPadding = LocalContentPadding.current

        val cellContentPadding = PaddingValues(
            acrossFrom = contentPadding.acrossFrom,
            acrossTo = contentPadding.acrossTo,
        )

        val lazyListContentPadding = PaddingValues(
            alongFrom = contentPadding.alongFrom,
            alongTo = contentPadding.alongTo,
        )

        val lazyListContent: LazyListScope.() -> Unit = {
            val scope = SLazyTableScopeImpl(
                lazyListScope = this,
                orientation = orientation,
                corners = corners,
                cellContentPadding = cellContentPadding,
            )
            scope.content()
            scope.apply()
        }


        orientation.fold(
            ifHorizontal = {
                LazyRow(
                    modifier = modifier,
                    state = state,
                    contentPadding = lazyListContentPadding,
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
                    contentPadding = lazyListContentPadding,
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
}

interface SLazyTableScope {

    val orientation: Orientation

    fun items(
        count: Int,
        key: ((index: Int) -> Any)? = null,
        contentType: (index: Int) -> Any? = { null },
        cellContent: @Composable LazyItemScope.(index: Int) -> Unit,
    )

    fun cells(
        count: Int,
        key: ((index: Int) -> Any)? = null,
        contentType: (index: Int) -> Any? = { null },
        cellContent: @Composable SLazyCellScope.(index: Int) -> Unit,
    )

    companion object
}

interface SLazyCellScope : LazyItemScope {

    val orientation: Orientation
}

fun <T> SLazyTableScope.cells(
    items: List<T>,
    key: ((T) -> Any)? = null,
    contentType: (T) -> Any? = { null },
    cellContent: @Composable (SLazyCellScope.(T) -> Unit)
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
    key: Any? = null,
    contentType: Any? = null,
    cellContent: @Composable SLazyCellScope.() -> Unit
) {
    cells(
        count = 1,
        key = key?.let { keyNotNull -> { keyNotNull } },
        contentType = { contentType },
        cellContent = { cellContent() },
    )
}

fun <T> SLazyTableScope.items(
    items: List<T>,
    key: ((T) -> Any)? = null,
    contentType: (T) -> Any? = { null },
    cellContent: @Composable LazyItemScope.(T) -> Unit,
) {
    items(
        count = items.size,
        key = key?.let { keyNotNull ->
            { index -> items[index].let(keyNotNull) }
        },
        contentType = { index -> items[index].let(contentType) },
        cellContent = { index -> cellContent(items[index]) },
    )
}

fun SLazyTableScope.item(
    key: Any? = null,
    contentType: Any? = null,
    cellContent: @Composable LazyItemScope.() -> Unit
) {
    items(
        count = 1,
        key = key?.let { keyNotNull -> { keyNotNull } },
        contentType = { contentType },
        cellContent = { cellContent() },
    )
}

fun SLazyTableScope.separator(
    key: Any? = null,
    contentType: Any? = null,
) {
    item(
        key = key,
        contentType = contentType,
    ) {
        Spacer(
            modifier = Modifier.size(
                size = LocalDistance.current.units.padding.along.medium,
            ),
        )
    }
}

@Composable
fun SLazyCellScope.Subtable(
    modifier: Modifier = Modifier,
    corners: ShapeCorners.Provider = LocalShapeCorners.current,
    reverseOrdering: Boolean = false,
    content: @Composable STableScope.() -> Unit,
) {
    val subtableOrientation = orientation.opposite
    STable(
        orientation = subtableOrientation,
        content = content,
        modifier = modifier.then(
            subtableOrientation.fold(
                ifHorizontal = { Modifier.fillMaxWidth() },
                ifVertical = { Modifier.fillMaxHeight() },
            )
        ),
        corners = corners,
        reverseOrdering = reverseOrdering,
    )
}

private class SLazyTableScopeImpl(
    private val lazyListScope: LazyListScope,
    override val orientation: Orientation,
    private val corners: ShapeCorners.Provider,
    private val cellContentPadding: PaddingValues,
) : SLazyTableScope {

    private sealed interface Element {

        data class Cells(
            val count: Int,
            val key: ((index: Int) -> Any)?,
            val contentType: (index: Int) -> Any?,
            val cellContent: @Composable (SLazyCellScope.(index: Int) -> Unit)
        ) : Element

        data class Items(
            val count: Int,
            val key: ((index: Int) -> Any)?,
            val contentType: (index: Int) -> Any?,
            val cellContent: @Composable LazyItemScope.(index: Int) -> Unit,
        ) : Element
    }

    private val elements: MutableList<Element> = mutableListOf<Element>()

    override fun cells(
        count: Int,
        key: ((index: Int) -> Any)?,
        contentType: (index: Int) -> Any?,
        cellContent: @Composable SLazyCellScope.(index: Int) -> Unit
    ) {
        elements.add(
            Element.Cells(
                count = count,
                key = key,
                contentType = contentType,
                cellContent = cellContent,
            )
        )
    }

    override fun items(
        count: Int,
        key: ((index: Int) -> Any)?,
        contentType: (index: Int) -> Any?,
        cellContent: @Composable LazyItemScope.(index: Int) -> Unit,
    ) {
        elements.add(
            Element.Items(
                count = count,
                key = key,
                contentType = contentType,
                cellContent = cellContent,
            )
        )
    }

    fun apply() {
        elements.forEachIndexed { elementIndex, element ->
            when (element) {
                is Element.Items -> {
                    lazyListScope.items(
                        count = element.count,
                        contentType = element.contentType,
                        key = element.key,
                    ) { cellIndex ->
                        CompositionLocalProvider(
                            LocalContentPadding provides cellContentPadding,
                        ) {
                            element.cellContent(this, cellIndex)
                        }
                    }
                }

                is Element.Cells -> {
                    val isFirstCells = when (elements.getOrNull(elementIndex - 1)) {
                        is Element.Cells -> false
                        null, is Element.Items -> true
                    }
                    val isLastCells = when (elements.getOrNull(elementIndex + 1)) {
                        is Element.Cells -> false
                        null, is Element.Items -> true
                    }
                    lazyListScope.items(
                        count = element.count,
                        contentType = element.contentType,
                        key = element.key,
                    ) { cellIndex ->
                        val isFirstCell = isFirstCells && cellIndex == 0
                        val isLastCell = isLastCells && cellIndex == element.count - 1

                        val cellScope = SLazyCellScopeImpl(
                            orientation = orientation,
                            lazyItemScope = this,
                        )

                        val cornersProvider = ShapeCorners.Provider {
                            corners
                                .getTableCorners()
                                .close(
                                    orientation = orientation,
                                    startOrTop = !isFirstCell,
                                    endOrBottom = !isLastCell,
                                )
                        }

                        CompositionLocalProvider(
                            LocalShapeCorners provides cornersProvider,
                            LocalContentPadding provides cellContentPadding,
                        ) {
                            Box(
                                modifier = orientation.fold(
                                    ifHorizontal = { Modifier.fillMaxHeight() },
                                    ifVertical = { Modifier.fillMaxWidth() },
                                ),
                                propagateMinConstraints = true,
                            ) {
                                element.cellContent(cellScope, cellIndex)
                            }
                        }
                    }
                }
            }
        }
    }
}

private class SLazyCellScopeImpl(
    private val lazyItemScope: LazyItemScope,
    override val orientation: Orientation,
) : LazyItemScope by lazyItemScope, SLazyCellScope