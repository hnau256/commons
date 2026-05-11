package org.hnau.commons.app.test.app.projector

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chair
import androidx.compose.material.icons.filled.CropDin
import androidx.compose.material.icons.filled.Earbuds
import androidx.compose.material.icons.filled.Mood
import androidx.compose.material.icons.filled.Save
import androidx.compose.runtime.Composable
import kotlinx.coroutines.CoroutineScope
import org.hnau.commons.app.projector.fractal.semantic.SContentWithActions
import org.hnau.commons.app.projector.fractal.semantic.SElements
import org.hnau.commons.app.projector.fractal.semantic.SScreen
import org.hnau.commons.app.projector.fractal.semantic.input.SInputProjector
import org.hnau.commons.app.projector.fractal.semantic.input.toErrorAsStringInputStateHolder
import org.hnau.commons.app.projector.fractal.semantic.input.toTextSInputProjectorFactory
import org.hnau.commons.app.projector.fractal.semantic.input.toFlagSInputProjectorFactory
import org.hnau.commons.app.projector.utils.Drawable
import org.hnau.commons.app.projector.utils.TitleOrIcon
import org.hnau.commons.app.test.app.model.FormModel
import org.hnau.commons.kotlin.coroutines.CancelOrInProgress

class FormProjector(
    scope: CoroutineScope,
    model: FormModel,
) {

    private val flag: SInputProjector = model
        .flag
        .toErrorAsStringInputStateHolder()
        .toFlagSInputProjectorFactory()
        .createSInputProjector(
            scope = scope,
            title = "Flag",
            icon = Drawable.Vector(Icons.Default.Mood),
        )


    private val decimal: SInputProjector = model
        .decimal
        .toErrorAsStringInputStateHolder { state, _ ->
            "Unable parse '$state' to BigDecimal"
        }
        .toTextSInputProjectorFactory()
        .createSInputProjector(
            scope = scope,
            title = "Decimal",
            icon = Drawable.Vector(Icons.Default.CropDin),
        )


    private val integer: SInputProjector = model
        .integer
        .toErrorAsStringInputStateHolder { state, _ ->
            "Unable parse '$state' to BigInteger"
        }
        .toTextSInputProjectorFactory()
        .createSInputProjector(
            scope = scope,
            title = "Integer",
            icon = Drawable.Vector(Icons.Default.Earbuds),
        )

    private val text = model
        .text
        .toErrorAsStringInputStateHolder { state, error ->
            "String '$state' is too short: expected at least ${error.expectedMinLength} characters, got ${error.actualLength}"
        }
        .toTextSInputProjectorFactory()
        .createSInputProjector(
            scope = scope,
            title = "Text",
            icon = Drawable.Vector(Icons.Default.Chair),
        )

    @Composable
    fun Content(
        contentPadding: PaddingValues,
    ) {
        SScreen(
            contentPadding = contentPadding,
        ) {
            SContentWithActions(
                content = {
                    SElements {
                        flag.Content()
                        decimal.Content()
                        integer.Content()
                        text.Content()
                    }
                },
                actions = {
                    Action<CancelOrInProgress.Cancel>(
                        actionOrElseOrDisabled = null,
                        titleOrIcon = TitleOrIcon.Both(
                            title = "Save",
                            icon = Drawable.Vector(Icons.Default.Save),
                        )
                    )
                }
            )
        }
    }

}