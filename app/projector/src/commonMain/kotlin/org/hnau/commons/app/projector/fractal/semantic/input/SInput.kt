package org.hnau.commons.app.projector.fractal.semantic.input

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import arrow.core.Option
import kotlinx.coroutines.flow.MutableStateFlow
import org.hnau.commons.app.projector.utils.TitleOrIcon

@Composable
fun <S, E, T> SInput(
    titleOrIcon: TitleOrIcon,
    type: SInputType<S, E, T>,
    state: MutableStateFlow<Option<S>>,
    modifier: Modifier = Modifier,
) {

}