package org.hnau.commons.app.projector.fractal

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.hnau.commons.app.model.theme.palette.PaletteType
import org.hnau.commons.app.projector.fractal.utils.LocalPalette

@Composable
fun FCheckBox(
    isChecked: StateFlow<Boolean>,
    modifier: Modifier = Modifier,
    palette: PaletteType = LocalPalette.current,
    onClick: (() -> Unit)? = null,
) {

}