package org.hnau.commons.app.projector.fractal.utils

fun Mood.activate(
    importance: Importance,
): Mood = fold(
    ifError = { Mood.Error },
    ifActive = {
        Mood.Active(
            importance = importance,
        )
    },
    ifNeutral = {
        Mood.Active(
            importance = importance,
        )
    },
)