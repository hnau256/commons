package org.hnau.commons.app.projector.uikit.line

import androidx.compose.ui.Modifier
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.ParentDataModifierNode
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.unit.Density
import org.hnau.commons.kotlin.castOrElse

context(_: LineScope)
fun Modifier.onPositionInLineChanged(
    onPositionCallback: ((LinePosition) -> Unit)?,
): Modifier = then(
    LinePositionElement(
        onPositionCallback = onPositionCallback,
    )
)

private data class LinePositionNode(
    var onPositionCallback: ((LinePosition) -> Unit)?,
) : Modifier.Node(), ParentDataModifierNode {
    override fun Density.modifyParentData(
        parentData: Any?,
    ): Any = parentData
        .castOrElse<LineParentData> { LineParentData.createEmpty() }
        .also { it.onPositionCallback = onPositionCallback }
}

private data class LinePositionElement(
    val onPositionCallback: ((LinePosition) -> Unit)?,
) : ModifierNodeElement<LinePositionNode>() {
    override fun create(): LinePositionNode = LinePositionNode(
        onPositionCallback = onPositionCallback,
    )

    override fun update(
        node: LinePositionNode,
    ) {
        node.onPositionCallback = onPositionCallback
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "Position"
        value = onPositionCallback
        properties["Position"] = onPositionCallback
    }
}