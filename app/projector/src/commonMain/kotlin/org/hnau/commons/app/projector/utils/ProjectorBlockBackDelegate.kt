package org.hnau.commons.app.projector.utils

import androidx.compose.runtime.Composable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import org.hnau.commons.app.model.utils.ModelBlockBackDelegate
import org.hnau.commons.app.projector.fractal.DialogContentInfo
import org.hnau.commons.app.projector.fractal.SDialog
import org.hnau.commons.app.projector.fractal.STableActionsScope
import org.hnau.commons.app.projector.uikit.table.TableScope
import org.hnau.commons.kotlin.coroutines.ActionOrElse
import org.hnau.commons.kotlin.coroutines.flow.state.mapState
import org.hnau.commons.kotlin.coroutines.instant

class ProjectorBlockBackDelegate<B>(
    scope: CoroutineScope,
    model: ModelBlockBackDelegate<B>,
    closeInfo: TitleOrIcon,
    content: @Composable TableScope.(blockReason: B) -> Unit,
    actions: @Composable STableActionsScope.(blockReason: B) -> Unit,
) {

    private val dialogInfo: StateFlow<DialogContentInfo?> = model
        .dialog
        .mapState(scope) { dialogOrNull ->
            dialogOrNull?.let { dialog ->
                val blockReason = dialog.blockReason
                val cancel = dialog.close
                DialogContentInfo(
                    content = { content(blockReason) },
                    cancel = cancel,
                    actions = {
                        Action(
                            actionOrElseOrDisabled = ActionOrElse.instant(cancel),
                            titleOrIcon = closeInfo,
                        )
                        actions(blockReason)
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