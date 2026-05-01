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
import org.hnau.commons.app.model.theme.palette.Palettes
import org.hnau.commons.app.projector.fractal.FBase
import org.hnau.commons.app.projector.fractal.FText
import org.hnau.commons.app.projector.fractal.semantic.SContentWithActions
import org.hnau.commons.app.projector.fractal.semantic.SMainWithAdditional
import org.hnau.commons.app.projector.fractal.semantic.utils.Importance
import org.hnau.commons.app.projector.utils.theme.local
import org.hnau.commons.app.test.app.model.RootModel
import org.hnau.commons.gen.pipe.annotations.Pipe
import org.hnau.commons.kotlin.coroutines.CancelOrInProgress

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
                            FText("Main")
                        },
                        additional = {
                            SContentWithActions(
                                content = {
                                    FText("Content")
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
                                        actionOrElseOrDisabled = null,
                                        titleOrIcon = Ior.Left("Secondary"),
                                        importance = Importance.Primary,
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
                        actionOrElseOrDisabled = null,
                        titleOrIcon = Ior.Left("Secondary"),
                        importance = Importance.Primary,
                    )
                }
            )
        }

    }
}