package org.hnau.commons.app.projector.fractal

/*@Composable
fun STitleOrIcon(
    titleOrIcon: TitleOrIcon,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.aligned(Alignment.CenterHorizontally),
    ) {
        val units = LocalDistance.current.units

        val iconOrNull = titleOrIcon.iconOrNull
        val titleOrNull = titleOrIcon.titleOrNull

        Side(
            isEnd = false,
            spaceHeight = units.iconSize,
            value = iconOrNull,
            content = ::SIcon,
        )

        val separation = units.padding.along.extraSmall
        separation
            .takeIf { iconOrNull != null && titleOrNull != null }
            .NullableStateContent(
                transitionSpec = TransitionSpec.rememberCenter(),
            ) { localSeparation ->
                Spacer(Modifier.width(localSeparation))
            }

        Side(
            isEnd = true,
            spaceHeight = null,
            value = titleOrNull,
            content = ::SText,
        )
    }
}

@Composable
private fun <T : Any> Side(
    isEnd: Boolean,
    spaceHeight: Dp?,
    value: T?,
    content: @Composable (T) -> Unit,
) {
    val alignment = isEnd.foldBoolean(
        ifTrue = { Alignment.CenterStart },
        ifFalse = { Alignment.CenterEnd },
    )
    value
        .StateContent(
            label = "STitleOrIconSide(isEnd=$isEnd)",
            contentKey = ::it,
            transitionSpec = TransitionSpec.remember(
                showAlignment = alignment,
                hideAlignment = alignment,
            ),
            contentAlignment = alignment,
        ) { localValueOrNull ->
            localValueOrNull.foldNullable(
                ifNull = {
                    Spacer(
                        Modifier
                            .option(spaceHeight?.let(Modifier::height))
                    )
                },
                ifNotNull = { localValue ->
                    content(localValue)
                }
            )
        }
}*/
