package org.hnau.commons.app.projector.fractal.utils

import org.hnau.commons.app.model.theme.color.Contrast


fun BaseWithDecay.Companion.contrast(
    initial: Contrast,
    decay: Double,
): BaseWithDecay<Contrast> = BaseWithDecay.double(
    initial = initial.contrast,
    decay = decay,
    baseline = Contrast.min.contrast
).map(::Contrast)



private val containerContrast: BaseWithDecay<Contrast> = BaseWithDecay.contrast(
    initial = Contrast(3.0),
    decay = 0.8,
)

val Contrast.Companion.container: BaseWithDecay<Contrast>
    get() = containerContrast

private val containerLowContrast: BaseWithDecay<Contrast> = BaseWithDecay.contrast(
    initial = Contrast(1.1),
    decay = 0.8,
)

val Contrast.Companion.containerLow: BaseWithDecay<Contrast>
    get() = containerLowContrast


val Contrast.Companion.content: BaseWithDecay<Contrast>
    get() = contentContrast

private val contentContrast: BaseWithDecay<Contrast> = BaseWithDecay.contrast(
    initial = Contrast(9.0),
    decay = 0.9,
)