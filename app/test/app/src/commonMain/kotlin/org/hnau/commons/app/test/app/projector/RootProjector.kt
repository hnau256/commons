package org.hnau.commons.app.test.app.projector

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import arrow.core.Ior
import kotlinx.coroutines.CoroutineScope
import org.hnau.commons.app.model.theme.palette.Palettes
import org.hnau.commons.app.model.toEditingString
import org.hnau.commons.app.projector.fractal.FBase
import org.hnau.commons.app.projector.fractal.FIcon
import org.hnau.commons.app.projector.fractal.FText
import org.hnau.commons.app.projector.fractal.FTextField
import org.hnau.commons.app.projector.fractal.semantic.SContentWithActions
import org.hnau.commons.app.projector.fractal.semantic.SElements
import org.hnau.commons.app.projector.fractal.semantic.SMainWithAdditional
import org.hnau.commons.app.projector.fractal.semantic.utils.Importance
import org.hnau.commons.app.projector.fractal.utils.LocalPalette
import org.hnau.commons.app.projector.fractal.utils.orError
import org.hnau.commons.app.projector.fractal.utils.size.TextStyleType
import org.hnau.commons.app.projector.utils.Icon
import org.hnau.commons.app.projector.utils.theme.local
import org.hnau.commons.app.test.app.model.RootModel
import org.hnau.commons.gen.pipe.annotations.Pipe
import org.hnau.commons.kotlin.coroutines.ActionOrElse
import org.hnau.commons.kotlin.coroutines.CancelOrInProgress
import org.hnau.commons.kotlin.coroutines.flow.state.mutable.toMutableStateFlowAsInitial
import org.hnau.commons.kotlin.ifTrue

class RootProjector(
    scope: CoroutineScope,
    dependencies: Dependencies,
    private val model: RootModel,
) {

    @Pipe
    interface Dependencies {

        companion object
    }

    @Composable
    fun Content(
        contentPadding: PaddingValues,
    ) {
        FBase(
            palettes = Palettes.local,
            modifier = Modifier.fillMaxSize().padding(contentPadding),
        ) {
            SContentWithActions(
                modifier = Modifier.fillMaxSize(),
                content = {
                    SMainWithAdditional(
                        main = {
                            SElements {
                                FText("Main title", type = TextStyleType.Title)
                                FText("Main")
                                val value = remember {
                                    "Qwerty"
                                        .toEditingString()
                                        .toMutableStateFlowAsInitial()
                                }
                                val containsDigits = value.collectAsState().value.text.let { it.any { it.isDigit() } }
                                FTextField(
                                    palette = LocalPalette.current.orError(containsDigits),
                                    startAccessory = {
                                        FIcon(
                                            image = Icons.Default.Settings,
                                        )
                                    },
                                    endAccessory = value.collectAsState().value.text.let { it.length > 2 }
                                        .ifTrue {
                                            {
                                                Icon(
                                                    icon = Icons.Default.Clear,
                                                    modifier = Modifier.clickable {
                                                        value.value = "".toEditingString()
                                                    }
                                                )
                                            }
                                        },
                                    topAccessory = { FText("Title") },
                                    bottomAccessory = containsDigits.ifTrue {
                                            {
                                                FText("Contains digits")
                                            }
                                        },
                                    value = value,
                                    modifier = Modifier.fillMaxWidth(),
                                )
                            }
                        },
                        additional = {
                            SContentWithActions(
                                content = { FText("Content") },
                                actions = {
                                    Action(
                                        actionOrElseOrDisabled = model.task.collectAsState().value,
                                        titleOrIcon = Ior.Both(
                                            leftValue = "Primary",
                                            rightValue = Icons.Default.Settings,
                                        ),
                                        importance = Importance.Primary,
                                    )
                                    Action<CancelOrInProgress.Cancel>(
                                        actionOrElseOrDisabled = ActionOrElse.Action {},
                                        titleOrIcon = Ior.Left("Secondary"),
                                        importance = Importance.Secondary,
                                    )
                                }
                            )
                        },
                    )
                },
                actions = {
                    Action(
                        actionOrElseOrDisabled = model.task.collectAsState().value,
                        titleOrIcon = Ior.Both(
                            leftValue = "Primary",
                            rightValue = Icons.Default.Settings,
                        ),
                        importance = Importance.Primary,
                    )
                    Action<CancelOrInProgress.Cancel>(
                        actionOrElseOrDisabled = ActionOrElse.Action {},
                        titleOrIcon = Ior.Left("Secondary"),
                        importance = Importance.Secondary,
                    )
                }
            )
        }

    }
}