package org.hnau.commons.app.projector.fractal.utils

import org.hnau.commons.gen.fold.annotations.Fold

@Fold
sealed interface Mood {

    val saturation: Saturation

    data object Neutral : Mood {

        override val saturation: Saturation
            get() = Saturation.Neutral
    }

    data object Error : Mood {

        override val saturation: Saturation
            get() = Saturation.Active
    }

    data class Active(
        val importance: Importance,
    ) : Mood {

        override val saturation: Saturation
            get() = Saturation.Active

        companion object {

            val default: Active = Active(
                importance = Importance.default,
            )
        }
    }

    companion object {

        val default: Mood
            get() = Neutral
    }
}