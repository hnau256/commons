package org.hnau.commons.app.test.app.projector

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Deblur
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import org.hnau.commons.app.projector.fractal.SButton
import org.hnau.commons.app.projector.fractal.SCell
import org.hnau.commons.app.projector.fractal.SCellBox
import org.hnau.commons.app.projector.fractal.SContentWithActions
import org.hnau.commons.app.projector.fractal.SElements
import org.hnau.commons.app.projector.fractal.SMainWithAdditional
import org.hnau.commons.app.projector.fractal.SScreen
import org.hnau.commons.app.projector.fractal.SText
import org.hnau.commons.app.projector.fractal.context.LocalFContext
import org.hnau.commons.app.projector.fractal.context.UpdateFContext
import org.hnau.commons.app.projector.fractal.size.SizeType
import org.hnau.commons.app.projector.fractal.size.units
import org.hnau.commons.app.projector.fractal.utils.Saturation
import org.hnau.commons.app.projector.uikit.line.weight
import org.hnau.commons.app.projector.uikit.table.Subtable
import org.hnau.commons.app.projector.uikit.table.Table
import org.hnau.commons.app.projector.utils.Drawable
import org.hnau.commons.app.projector.utils.Orientation
import org.hnau.commons.app.projector.utils.TitleOrIcon
import org.hnau.commons.app.test.app.model.ActionModel
import org.hnau.commons.kotlin.KeyValue
import org.hnau.commons.kotlin.coroutines.ActionOrElse
import org.hnau.commons.kotlin.coroutines.flow.state.mapState
import org.hnau.commons.kotlin.coroutines.instant
import org.hnau.commons.kotlin.coroutines.noAction

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
            title = { SText("Action") },
        ) {
            SContentWithActions(
                content = {
                    SMainWithAdditional(
                        main = { Box {} },
                        additional = {
                            SElements {
                                Table(
                                    separation = LocalFContext.current.distance.units.borderWidth,
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
                                    SCell { modifier, shape ->
                                        SButton(
                                            modifier = modifier,
                                            actionOrElseOrDisabled = ActionOrElse.instant(model.editConfig),
                                            titleOrIcon = TitleOrIcon.Both(
                                                title = "Edit",
                                                icon = Drawable.Vector(Icons.Default.Edit),
                                            ),
                                            shape = shape,
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