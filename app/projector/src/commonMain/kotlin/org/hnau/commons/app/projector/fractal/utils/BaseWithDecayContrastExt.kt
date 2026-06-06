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


private val containerContrast: SaturationValues<BaseWithDecay<Contrast>> = SaturationValues(
    neutral = BaseWithDecay.contrast(
        initial = Contrast(1.1),
        decay = 0.8,
    ),
    active = BaseWithDecay.contrast(
        initial = Contrast(3.0),
        decay = 0.8,
    ),
)

val Contrast.Companion.container: SaturationValues<BaseWithDecay<Contrast>>
    get() = containerContrast

private val contentContrast: SaturationValues<BaseWithDecay<Contrast>> = SaturationValues(
    neutral = BaseWithDecay.contrast(
        initial = Contrast(5.0),
        decay = 0.9,
    ),
    active = BaseWithDecay.contrast(
        initial = Contrast(9.0),
        decay = 0.9,
    ),
)

val Contrast.Companion.content: SaturationValues<BaseWithDecay<Contrast>>
    get() = contentContrast