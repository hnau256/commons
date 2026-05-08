package org.hnau.commons.app.test.app.projector

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import arrow.core.prependTo
import kotlinx.coroutines.CoroutineScope
import org.hnau.commons.app.model.theme.palette.PaletteType
import org.hnau.commons.app.model.theme.palette.Palettes
import org.hnau.commons.app.model.toEditingString
import org.hnau.commons.app.projector.fractal.FBase
import org.hnau.commons.app.projector.fractal.FCheckBox
import org.hnau.commons.app.projector.fractal.FIcon
import org.hnau.commons.app.projector.fractal.FText
import org.hnau.commons.app.projector.fractal.FTextField
import org.hnau.commons.app.projector.fractal.context.FContext
import org.hnau.commons.app.projector.fractal.context.LocalFContext
import org.hnau.commons.app.projector.fractal.semantic.SContentWithActions
import org.hnau.commons.app.projector.fractal.semantic.SElements
import org.hnau.commons.app.projector.fractal.semantic.SMainWithAdditional
import org.hnau.commons.app.projector.fractal.semantic.utils.Importance
import org.hnau.commons.app.projector.fractal.size.SizeType
import org.hnau.commons.app.projector.fractal.utils.Distance
import org.hnau.commons.app.projector.fractal.utils.orError
import org.hnau.commons.app.projector.utils.Drawable
import org.hnau.commons.app.projector.utils.Icon
import org.hnau.commons.app.projector.utils.TitleOrIcon
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
        palettes: Palettes,
        contentPadding: PaddingValues,
    ) {
        FBase(
            context = FContext(
                distance = Distance.zero,
                palettes = palettes,
            ),
            modifier = Modifier.fillMaxSize().padding(contentPadding),
        ) {
            SContentWithActions(
                modifier = Modifier.fillMaxSize(),
                content = {
                    SMainWithAdditional(
                        main = {
                            SElements {
                                FText("Main title", type = SizeType.Large)
                                FText("Main")
                                val value = remember {
                                    "Qwerty"
                                        .toMutableStateFlowAsInitial()
                                }
                                val containsDigits =
                                    value.collectAsState().value.let { it.any { it.isDigit() } }
                                FTextField(
                                    palette = PaletteType.Primary.orError(containsDigits),
                                    startAccessory = {
                                        FIcon(
                                            drawable = Drawable.Vector(Icons.Default.Settings),
                                        )
                                    },
                                    endAccessory = value.collectAsState().value.let { it.length > 2 }
                                        .ifTrue {
                                            {
                                                Icon(
                                                    icon = Icons.Default.Clear,
                                                    modifier = Modifier.clickable {
                                                        value.value = ""
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
                                Box {
                                    var isChecked by remember { mutableStateOf(false) }
                                    FCheckBox(
                                        isChecked = isChecked,
                                        onClick = { isChecked = !isChecked }
                                    )
                                }
                            }
                        },
                        additional = {
                            SContentWithActions(
                                content = { FText("Content") },
                                actions = {
                                    Action(
                                        actionOrElseOrDisabled = model.task.collectAsState().value,
                                        titleOrIcon = TitleOrIcon.Both(
                                            title = "Primary",
                                            icon = Drawable.Vector(Icons.Default.Settings),
                                        ),
                                        importance = Importance.Primary,
                                    )
                                    Action<CancelOrInProgress.Cancel>(
                                        actionOrElseOrDisabled = ActionOrElse.Action {},
                                        titleOrIcon = TitleOrIcon.Title("Secondary"),
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
                        titleOrIcon = TitleOrIcon.Both(
                            title = "Primary",
                            icon = Drawable.Vector(Icons.Default.Settings),
                        ),
                        importance = Importance.Primary,
                    )
                    Action<CancelOrInProgress.Cancel>(
                        actionOrElseOrDisabled = ActionOrElse.Action {},
                        titleOrIcon = TitleOrIcon.Title("Secondary"),
                        importance = Importance.Secondary,
                    )
                }
            )
        }

    }
}