package hnau.common.app.projector.uikit

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import hnau.common.app.projector.uikit.utils.Dimens
import hnau.common.app.projector.utils.horizontalDisplayPadding

@Composable
fun TopBar(
    modifier: Modifier,
    content: @Composable RowScope.() -> Unit,
) {
    Row(
        modifier = modifier
            .height(TopBarDefaults.height)
            .fillMaxWidth()
            .padding(horizontal = TopBarDefaults.separation),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(
            space = Dimens.smallSeparation,
            alignment = Alignment.Start,
        ),
    ) {
        CompositionLocalProvider(
            LocalContentColor provides TopBarDefaults.itemContentColor
        ) {
            content()
        }
    }
}

@Composable
fun RowScope.TopBarTitle(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit,
) {
    Row(
        modifier = modifier
            .weight(1f)
            .fillMaxHeight(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CompositionLocalProvider(
            LocalTextStyle provides MaterialTheme.typography.titleLarge,
        ) {
            content()
        }
    }
}

@Composable
fun TopBarAction(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)?,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = modifier
            .clip(TopBarDefaults.itemShape)
            .clickable(
                onClick = { onClick?.invoke() },
                enabled = onClick != null,
            )
            .background(TopBarDefaults.itemContainerColor)
            .size(TopBarDefaults.height),
        contentAlignment = Alignment.Center,
        content = content,
    )
}

@Composable
private fun Modifier.topBarItemBackground(): Modifier = background(
    color = TopBarDefaults.itemContainerColor,
    shape = TopBarDefaults.itemShape,
)

data object TopBarDefaults {

    val itemShape: Shape = RoundedCornerShape(
        percent = 100,
    )

    val itemContainerColor: Color
        @Composable
        get() = MaterialTheme.colorScheme.surfaceContainerHighest

    val itemContentColor: Color
        @Composable
        get() = MaterialTheme.colorScheme.onSurface

    val separation: Dp
        get() = Dimens.horizontalDisplayPadding

    val height: Dp = 48.dp
}