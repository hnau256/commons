package org.hnau.commons.app.projector.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Save
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import org.hnau.commons.app.model.utils.ModelSavableDelegate
import org.hnau.commons.app.projector.fractal.DialogContentInfo
import org.hnau.commons.app.projector.fractal.SButton
import org.hnau.commons.app.projector.fractal.SCell
import org.hnau.commons.app.projector.fractal.SDialog
import org.hnau.commons.app.projector.fractal.context.UpdateFContext
import org.hnau.commons.app.projector.fractal.utils.Mood
import org.hnau.commons.app.projector.uikit.table.TableScope
import org.hnau.commons.kotlin.coroutines.ActionOrElse
import org.hnau.commons.kotlin.coroutines.flow.state.mapState
import org.hnau.commons.kotlin.coroutines.instant

class ProjectorSavableDelegate<T>(
    scope: CoroutineScope,
    model: ModelSavableDelegate<T>,
    notSaved: @Composable TableScope.() -> Unit,
    save: String,
    edit: String,
    reset: String,
) {

    private val dialogInfo: StateFlow<DialogContentInfo?> = model
        .dialog
        .mapState(scope) { dialogOrNull ->
            dialogOrNull?.let { dialog ->
                val cancel = dialog.returnToEditing
                DialogContentInfo(
                    content = notSaved,
                    cancel = cancel,
                    actions = {
                        dialog.saveAndExitIfPossible?.let { saveAndExitIfPossible ->
                            UpdateFContext(
                                mood = Mood.Primary,
                            ) {
                                SCell { modifier, shape ->
                                    SButton(
                                        modifier = modifier,
                                        shape = shape,
                                        actionOrElseOrDisabled = saveAndExitIfPossible.collectAsState().value,
                                        titleOrIcon = TitleOrIcon.Both(
                                            title = save,
                                            icon = Drawable.Vector(Icons.Default.Save),
                                        ),
                                    )
                                }
                            }
                        }
                        UpdateFContext(
                            mood = Mood.Tertiary,
                        ) {
                            SCell { modifier, shape ->
                                SButton(
                                    modifier = modifier,
                                    shape = shape,
                                    actionOrElseOrDisabled = ActionOrElse.instant(cancel),
                                    titleOrIcon = TitleOrIcon.Both(
                                        title = edit,
                                        icon = Drawable.Vector(Icons.Default.Edit),
                                    ),
                                )
                            }
                        }
                        UpdateFContext(
                            mood = Mood.Error,
                        ) {
                            SCell { modifier, shape ->
                                SButton(
                                    modifier = modifier,
                                    shape = shape,
                                    actionOrElseOrDisabled = ActionOrElse.instant(dialog.exitWithoutSaving),
                                    titleOrIcon = TitleOrIcon.Both(
                                        title = reset,
                                        icon = Drawable.Vector(Icons.Default.Clear),
                                    ),
                                )
                            }
                        }
                    }
                )
            }
        }

    @Composable
    fun Dialog() {
        SDialog(
            info = dialogInfo,
        )
    }
}