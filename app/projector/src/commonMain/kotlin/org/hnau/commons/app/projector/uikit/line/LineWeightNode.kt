package org.hnau.commons.app.projector.uikit.line

import androidx.compose.ui.Modifier
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.ParentDataModifierNode
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.unit.Density
import org.hnau.commons.kotlin.castOrElse

context(_: LineScope)
fun Modifier.weight(
    weight: Float,
): Modifier = then(
    LineWeightElement(
        weight = weight.coerceAtMost(Float.MAX_VALUE),
    )
)

private data class LineWeightNode(
    var weight: Float,
) : Modifier.Node(), ParentDataModifierNode {
    override fun Density.modifyParentData(
        parentData: Any?,
    ): Any = parentData
        .castOrElse<LineParentData> { LineParentData.createEmpty() }
        .also { it.weight = weight }
}

private data class LineWeightElement(
    val weight: Float,
) : ModifierNodeElement<LineWeightNode>() {
    override fun create(): LineWeightNode = LineWeightNode(
        weight = weight,
    )

    override fun update(
        node: LineWeightNode,
    ) {
        node.weight = weight
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "weight"
        value = weight
        properties["weight"] = weight
    }
}