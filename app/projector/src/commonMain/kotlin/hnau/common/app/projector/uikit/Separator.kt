package hnau.common.app.projector.uikit

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import hnau.common.app.projector.uikit.utils.Dimens

@Composable
fun Separator(
    size: Dp = Dimens.separation,
) = Spacer(
    modifier = Modifier.size(size),
)
