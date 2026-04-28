package org.hnau.commons.app.test.app.projector

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import arrow.core.Ior
import kotlinx.coroutines.CoroutineScope
import org.hnau.commons.app.model.theme.palette.PaletteType
import org.hnau.commons.app.model.theme.palette.Palettes
import org.hnau.commons.app.projector.fractal.FBase
import org.hnau.commons.app.projector.fractal.FButton
import org.hnau.commons.app.projector.fractal.FColumn
import org.hnau.commons.app.projector.utils.theme.local
import org.hnau.commons.app.test.app.model.RootModel
import org.hnau.commons.gen.pipe.annotations.Pipe

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
            modifier = Modifier.fillMaxSize(),
        ) {
            FColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding),
            ) {
                FButton(
                    actionOrElseOrDisabled = model.task.collectAsState().value,
                    titleOrIcon = Ior.Both(
                        leftValue = "Settings",
                        rightValue = Icons.Default.Settings,
                    ),
                    palette = PaletteType.Primary,
                )
            }
        }

    }
}