package hnau.common.app.projector.uikit.table

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

object TableDefaults {

    val cellColor: Color
        @Composable
        get() = MaterialTheme.colorScheme.surfaceContainerLow
}