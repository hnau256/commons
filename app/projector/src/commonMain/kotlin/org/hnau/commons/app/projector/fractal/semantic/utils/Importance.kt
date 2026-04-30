package org.hnau.commons.app.projector.fractal.semantic.utils

enum class Importance {
    Primary, Secondary, Tertiary, Quaternary;

    companion object {

        val default: Importance
            get() = Primary
    }
}

inline fun <R> Importance.fold(
    ifPrimary: () -> R,
    ifSecondary: () -> R,
    ifTertiary: () -> R,
    ifQuaternary: () -> R,
): R = when (this) {
    Importance.Primary -> ifPrimary()
    Importance.Secondary -> ifSecondary()
    Importance.Tertiary -> ifTertiary()
    Importance.Quaternary -> ifQuaternary()
}