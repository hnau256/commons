package org.hnau.commons.app.test.app.projector

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import kotlinx.coroutines.CoroutineScope
import org.hnau.commons.app.projector.uikit.backbutton.BackButtonHost
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

    private val form = FormProjector(
        scope = scope,
        model = model.form,
    )

    @Composable
    fun Content(
        contentPadding: PaddingValues,
    ) {
        BackButtonHost(
            contentPadding = contentPadding,
            goBackHandler = model.goBackHandler,
        ) { contentPadding ->
            form.Content(
                contentPadding = contentPadding
            )
        }
    }
}