package org.hnau.commons.app.test.app.projector

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import org.hnau.commons.app.projector.stack.Content
import org.hnau.commons.app.projector.stack.StackProjectorTail
import org.hnau.commons.app.test.app.model.RootStackElementModel
import org.hnau.commons.app.test.app.model.RootStackModel
import org.hnau.commons.app.test.app.model.fold
import org.hnau.commons.gen.pipe.annotations.Pipe
import org.hnau.commons.gen.sealup.annotations.SealUp
import org.hnau.commons.gen.sealup.annotations.Variant

class RootStackProjector(
    private val scope: CoroutineScope,
    private val model: RootStackModel,
    private val dependencies: Dependencies,
) {

    @Pipe
    interface Dependencies

    @SealUp(
        variants = [
            Variant(
                type = ActionProjector::class,
                identifier = "action",
            ),
            Variant(
                type = FormProjector::class,
                identifier = "form",
            ),
        ],
        wrappedValuePropertyName = "projector",
        sealedInterfaceName = "RootStackElementProjector",
    )
    interface Element {

        @Composable
        fun Content(
            contentPadding: PaddingValues,
        )

        companion object
    }

    private val tail: StateFlow<StackProjectorTail<Int, RootStackElementProjector>> =
        StackProjectorTail(
            scope = scope,
            modelsStack = model.stack,
            extractKey = RootStackElementModel::ordinal,
            createProjector = { scope, model ->
                model.fold(
                    ifAction = { actionModel ->
                        Element.action(
                            scope = scope,
                            model = actionModel,
                        )
                    },
                    ifForm = { formModel ->
                        Element.form(
                            scope = scope,
                            model = formModel,
                        )
                    },
                )
            }
        )

    @Composable
    fun Content(
        contentPadding: PaddingValues,
    ) {
        tail.Content { elementProjector ->
            elementProjector.Content(
                contentPadding = contentPadding,
            )
        }
    }
}
