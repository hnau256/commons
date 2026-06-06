package org.hnau.commons.app.projector.fractal.utils

import org.hnau.commons.kotlin.foldNullable

fun Mood.activateIfNeed(
    importance: Importance?,
): Mood = fold(
    ifError = { Mood.Error },
    ifActive = { currentImportance ->
        Mood.Active(
            importance = importance ?: currentImportance,
        )
    },
    ifNeutral = {
        importance.foldNullable(
            ifNull = { Mood.Neutral },
            ifNotNull = { importanceNotNull ->
                Mood.Active(
                    importance = importanceNotNull,
                )
            }
        )
    },
)