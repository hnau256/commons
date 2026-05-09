package org.hnau.commons.app.projector.fractal.utils

enum class Mood {
    Primary, Secondary, Tertiary, Error;

    companion object {

        val default: Mood
            get() = Primary
    }
}

inline fun <R> Mood.fold(
    ifPrimary: () -> R,
    ifSecondary: () -> R,
    ifTertiary: () -> R,
    ifError: () -> R,
): R = when (this) {
    Mood.Primary -> ifPrimary()
    Mood.Secondary -> ifSecondary()
    Mood.Tertiary -> ifTertiary()
    Mood.Error -> ifError()
}