package org.hnau.commons.app.projector.uikit.line

import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.ParentDataModifierNode
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.unit.Density
import org.hnau.commons.kotlin.castOrElse

context(_: LineScope)
fun Modifier.onRectInLineChanged(
    onRectCallback: (Rect) -> Unit,
): Modifier = then(
    LineRectElement(
        onRectCallback = onRectCallback,
    )
)

private data class LineRectNode(
    var onRectCallback: (Rect) -> Unit,
) : Modifier.Node(), ParentDataModifierNode {
    override fun Density.modifyParentData(
        parentData: Any?,
    ): Any = parentData
        .castOrElse<LineParentData> { LineParentData.createEmpty() }
        .also { it.onRectCallback = onRectCallback }
}

private data class LineRectElement(
    val onRectCallback: (Rect) -> Unit,
) : ModifierNodeElement<LineRectNode>() {
    override fun create(): LineRectNode = LineRectNode(
        onRectCallback = onRectCallback,
    )

    override fun update(
        node: LineRectNode,
    ) {
        node.onRectCallback = onRectCallback
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "Rect"
        value = onRectCallback
        properties["onRectCallback"] = onRectCallback
    }
}
