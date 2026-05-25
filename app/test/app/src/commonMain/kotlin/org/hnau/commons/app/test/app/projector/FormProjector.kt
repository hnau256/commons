package org.hnau.commons.app.test.app.projector

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chair
import androidx.compose.material.icons.filled.CropDin
import androidx.compose.material.icons.filled.Earbuds
import androidx.compose.material.icons.filled.Mood
import androidx.compose.material.icons.filled.Save
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import kotlinx.coroutines.CoroutineScope
import org.hnau.commons.app.projector.fractal.SCellBox
import org.hnau.commons.app.projector.fractal.SContentWithActions
import org.hnau.commons.app.projector.fractal.SScreen
import org.hnau.commons.app.projector.fractal.STable
import org.hnau.commons.app.projector.fractal.SText
import org.hnau.commons.app.projector.fractal.input.InputProjector
import org.hnau.commons.app.projector.fractal.input.createInputProjector
import org.hnau.commons.app.projector.fractal.input.type.toInputProjectorPrototype
import org.hnau.commons.app.projector.fractal.size.SizeType
import org.hnau.commons.app.projector.utils.Drawable
import org.hnau.commons.app.projector.utils.Orientation
import org.hnau.commons.app.projector.utils.ProjectorSavableDelegate
import org.hnau.commons.app.projector.utils.TitleOrIcon
import org.hnau.commons.app.test.app.model.Config
import org.hnau.commons.app.test.app.model.FormModel

class FormProjector(
    scope: CoroutineScope,
    private val model: FormModel,
) {

    private val flag: InputProjector = model
        .flag
        .toInputProjectorPrototype()
        .createInputProjector(
            scope = scope,
            title = "Flag",
            icon = Drawable.Vector(Icons.Default.Mood),
        )


    private val decimal: InputProjector = model
        .decimal
        .toInputProjectorPrototype(
            imeAction = ImeAction.Next,
        )
        .createInputProjector(
            scope = scope,
            title = "Decimal",
            icon = Drawable.Vector(Icons.Default.CropDin),
        ) { state, _ ->
            "Unable parse '$state' to BigDecimal"
        }


    private val integer: InputProjector = model
        .integer
        .toInputProjectorPrototype(
            imeAction = ImeAction.Next,
        )
        .createInputProjector(
            scope = scope,
            title = "Integer",
            icon = Drawable.Vector(Icons.Default.Earbuds),
        ) { state, _ ->
            "Unable parse '$state' to BigInteger"
        }

    private val text = model
        .text
        .toInputProjectorPrototype(
            imeAction = ImeAction.Done,
        )
        .createInputProjector(
            scope = scope,
            title = "Text",
            icon = Drawable.Vector(Icons.Default.Chair),
        ) { state, error ->
            "String '$state' is too short: expected at least ${error.expectedMinLength} characters, got ${error.actualLength}"
        }

    private val savableDelegate: ProjectorSavableDelegate<Config> = ProjectorSavableDelegate(
        scope = scope,
        model = model.savableDelegate,
        notSaved = {
            SCellBox {
                SText(
                    text = "Config is not saved",
                    type = SizeType.Large,
                )
            }
        },
        save = "Save",
        edit = "Edit",
        reset = "Reset",
    )

    @Composable
    fun Content(
        contentPadding: PaddingValues,
    ) {
        SScreen(
            contentPadding = contentPadding,
            title = { SText("Form") },
        ) { contentPadding ->
            SContentWithActions(
                modifier = Modifier.padding(contentPadding),
                content = {
                    STable(
                        orientation = Orientation.Vertical,
                    ) {
                        with(flag) { Content() }
                        with(decimal) { Content() }
                        with(integer) { Content() }
                        with(text) { Content() }
                    }
                },
                actions = {
                    Action(
                        actionOrElseOrDisabled = model
                            .savableDelegate
                            .saveOrInactive
                            .collectAsState()
                            .value
                            ?.collectAsState()
                            ?.value,
                        titleOrIcon = TitleOrIcon.Both(
                            title = "Save",
                            icon = Drawable.Vector(Icons.Default.Save),
                        )
                    )
                }
            )
            savableDelegate.Dialog()
        }
    }

}