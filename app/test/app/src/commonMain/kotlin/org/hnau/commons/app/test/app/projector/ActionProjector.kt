package org.hnau.commons.app.test.app.projector

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import org.hnau.commons.app.projector.fractal.SActions
import org.hnau.commons.app.projector.fractal.SCellBox
import org.hnau.commons.app.projector.fractal.SContentWithActions
import org.hnau.commons.app.projector.fractal.SElements
import org.hnau.commons.app.projector.fractal.SMainWithAdditional
import org.hnau.commons.app.projector.fractal.SScreen
import org.hnau.commons.app.projector.fractal.STable
import org.hnau.commons.app.projector.fractal.STabs
import org.hnau.commons.app.projector.fractal.SText
import org.hnau.commons.app.projector.fractal.context.UpdateFContext
import org.hnau.commons.app.projector.fractal.size.SizeType
import org.hnau.commons.app.projector.fractal.utils.Saturation
import org.hnau.commons.app.projector.uikit.line.weight
import org.hnau.commons.app.projector.uikit.table.Subtable
import org.hnau.commons.app.projector.utils.Drawable
import org.hnau.commons.app.projector.utils.Orientation
import org.hnau.commons.app.projector.utils.TitleOrIcon
import org.hnau.commons.app.test.app.model.ActionModel
import org.hnau.commons.kotlin.KeyValue
import org.hnau.commons.kotlin.coroutines.ActionOrElse
import org.hnau.commons.kotlin.coroutines.flow.state.mapState
import org.hnau.commons.kotlin.coroutines.instant

class ActionProjector(
    scope: CoroutineScope,
    private val model: ActionModel,
) {

    private val configItems: StateFlow<List<KeyValue<String, String>>> = model
        .config
        .mapState(scope) { config ->
            listOf(
                "Flag" to config.flag.toString(),
                "Decimal" to config.decimal.toStringExpanded(),
                "Integer" to config.integer.toString(),
                "Text" to config.text,
            ).map { (title, value) ->
                KeyValue(title, value)
            }
        }

    @Composable
    fun Content(
        contentPadding: PaddingValues
    ) {
        SScreen(
            contentPadding = contentPadding,
            title = {
                val items = remember { listOf("One", "Two", "Three") }
                var selected by remember { mutableStateOf(items.first()) }
                STabs(
                    items = items,
                    selected = selected,
                    onSelectedChanged = { selected = it },
                ) { item ->
                    SText(item)
                }
            },
        ) { contentPadding ->
            SContentWithActions(
                modifier = Modifier.padding(contentPadding),
                content = {
                    SMainWithAdditional(
                        main = { Box {} },
                        additional = {
                            SElements {
                                STable(
                                    orientation = Orientation.Vertical,
                                ) {
                                    SCellBox(
                                        contentAlignment = Alignment.CenterStart,
                                    ) {
                                        UpdateFContext(
                                            saturation = Saturation.Active,
                                        ) {
                                            SText(
                                                text = "Config",
                                                type = SizeType.Large,
                                            )
                                        }
                                    }
                                    Subtable {
                                        val rows = configItems
                                            .collectAsState()
                                            .value
                                        Subtable {
                                            rows.forEach { (title) ->
                                                SCellBox(
                                                    contentAlignment = Alignment.CenterStart,
                                                ) {
                                                    SText(title)
                                                }
                                            }
                                        }
                                        Subtable(
                                            modifier = Modifier.weight(1f),
                                        ) {
                                            rows.forEach { (_, value) ->
                                                SCellBox(
                                                    contentAlignment = Alignment.CenterEnd,
                                                ) {
                                                    UpdateFContext(
                                                        saturation = Saturation.Active,
                                                    ) {
                                                        SText(value)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    SActions {
                                        Action(
                                            actionOrElseOrDisabled = ActionOrElse.instant(model.editConfig),
                                            titleOrIcon = TitleOrIcon.Both(
                                                title = "Edit",
                                                icon = Drawable.Vector(Icons.Default.Edit),
                                            ),
                                        )
                                    }
                                }
                            }
                        }
                    )
                },
                actions = {
                    Action(
                        actionOrElseOrDisabled = model.doAction.collectAsState().value,
                        titleOrIcon = TitleOrIcon.Both(
                            title = "Do action",
                            icon = Drawable.Vector(Icons.Default.RocketLaunch),
                        ),
                    )
                }
            )
        }
    }
}