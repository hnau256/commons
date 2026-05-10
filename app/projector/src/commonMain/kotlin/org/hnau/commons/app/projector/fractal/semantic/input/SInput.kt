package org.hnau.commons.app.projector.fractal.semantic.input

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import arrow.optics.Lens
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import org.hnau.commons.app.projector.fractal.FIcon
import org.hnau.commons.app.projector.fractal.FItem
import org.hnau.commons.app.projector.fractal.FText
import org.hnau.commons.app.projector.utils.TitleOrIcon
import org.hnau.commons.app.projector.utils.collectAsMutableAccessor
import org.hnau.commons.app.projector.utils.iconOrNull
import org.hnau.commons.app.projector.utils.rememberLet
import org.hnau.commons.app.projector.utils.rememberRun
import org.hnau.commons.app.projector.utils.titleOrNull
import org.hnau.commons.kotlin.MutableAccessor
import org.hnau.commons.kotlin.coroutines.flow.state.Stickable
import org.hnau.commons.kotlin.coroutines.flow.state.mutable.toMutableStateFlowAsInitial
import org.hnau.commons.kotlin.coroutines.flow.state.stick
import org.hnau.commons.kotlin.extract
import org.hnau.commons.kotlin.ifTrue

@Composable
private fun <S, E, V> SInput(
    state: SMutableInputState<S, E, V>,
) {
    when (state.type) {
        is SInputType.Edit -> TODO()
        SInputType.Flag -> TODO()
    }
}

@Composable
fun <S, E, V> SInput(
    titleOrIcon: TitleOrIcon,
    state: MutableStateFlow<SInputState<S, E, V>>,
    modifier: Modifier = Modifier,
) {

    val scope = rememberCoroutineScope()
    val mutableState by remember(scope, state) {
        state.toMutableState(scope)
    }.collectAsState()

    when (type) {
        is SInputType.Edit -> TODO()
        SInputType.Flag -> TODO()
    }

    val stateAccessor = state.collectAsMutableAccessor()
    var currentState: SInputState<S, E, V> by stateAccessor

    val valueAccessor: MutableAccessor<S> = stateAccessor.rememberRun {
        extract(
            Lens(
                get = SInputState<S, E, V>::state,
                set = { state, value ->
                    state.copy(
                        state = value
                    )
                }
            )
        )
    }

    val content = currentState
        .type
        .rememberLet(valueAccessor) { type ->
            SInputContent.create(
                type = type,
                value = valueAccessor,
            )
        }

    FItem(
        modifier = modifier,
        startAccessory = titleOrIcon.iconOrNull?.let { icon ->
            { FIcon(icon) }
        },
        topAccessory = content.fold(
            ifWithTitle = { null },
            ifWithoutTitle = {
                titleOrIcon.titleOrNull?.let { title ->
                    {
                        FText(
                            text = title
                        )
                    }
                }
            }
        ),
        bottomAccessory = currentState.valueOrError.fold(
            ifLeft = { error ->
                {
                    val errorMessage = currentState.type.mapper.convertErrorToString(error)
                    FText(
                        text = errorMessage
                    )
                }
            },
            ifRight = { null }
        )
    ) {

    }
}

internal interface ItemDrawer {

    @Composable
    fun Item(
        content: @Composable () -> Unit,
        endAccessor: ((@Composable () -> Unit) -> Unit)?,
    )
}

internal sealed interface SInputContent {

    data class WithTitle(
        val content: @Composable (
            title: String?,
            item: ItemDrawer,
        ) -> Unit,
    ) : SInputContent

    data class WithoutTitle(
        val content: @Composable (
            item: ItemDrawer,
        ) -> Unit,
    ) : SInputContent

    companion object {

        fun <S, E, V> create(
            scope: CoroutineScope,
            state: MutableStateFlow<SInputState<S, E, V>>,
        ) = state.stick(
            scope = scope,
        ) { scope, initial ->
            when (initial.type) {
                is SInputType.Edit -> object :
                    Stickable<SInputState<S, E, V>, SInputState<S, E, V>> {

                    private val localState: MutableStateFlow<S> =
                        initial.state.toMutableStateFlowAsInitial()

                    override fun tryUpdateValue(
                        newValue: SInputState<S, E, V>,
                    ): Boolean = (newValue.type is SInputType.Edit<E, V>).apply {
                        ifTrue {
                            localState.value = newValue.state
                        }
                    }

                    override val result: SInputState<S, E, V>
                        get() = TODO("Not yet implemented")

                }

                SInputType.Flag -> TODO()
            }
        }
    }
}

internal inline fun <R> SInputContent.fold(
    ifWithTitle: (@Composable (title: String?) -> Unit) -> R,
    ifWithoutTitle: (@Composable () -> Unit) -> R,
): R = when (this) {
    is SInputContent.WithTitle -> ifWithTitle(content)
    is SInputContent.WithoutTitle -> ifWithoutTitle(content)
}