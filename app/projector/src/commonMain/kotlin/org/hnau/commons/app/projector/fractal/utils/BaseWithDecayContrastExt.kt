package org.hnau.commons.app.projector.fractal.utils

import org.hnau.commons.app.model.theme.color.Contrast


fun BaseWithDecay.Companion.contrast(
    initial: Contrast,
    decay: Double,
): BaseWithDecay<Contrast> = BaseWithDecay.int(
    initial = initial.contrast,
    decay = decay,
    baseline = Contrast.min.contrast
).map(::Contrast)



private val containerContrast: BaseWithDecay<Contrast> = BaseWithDecay.contrast(
    initial = Contrast(3),
    decay = 0.8,
)

val Contrast.Companion.container: BaseWithDecay<Contrast>
    get() = containerContrast

private val contentBySaturationContrast: SaturationValues<BaseWithDecay<Contrast>> = SaturationValues(
    active = BaseWithDecay.contrast(
        initial = Contrast(7),
        decay = 0.9,
    ),
    neutral = BaseWithDecay.contrast(
        initial = Contrast(3),
        decay = 0.9,
    ),
)



val Contrast.Companion.contentBySaturation: SaturationValues<BaseWithDecay<Contrast>>
    get() = contentBySaturationContrast

private val contentLowContrast: BaseWithDecay<Contrast> = BaseWithDecay.contrast(
    initial = Contrast(5),
    decay = 0.9,
)

val Contrast.Companion.contentLow: BaseWithDecay<Contrast>
    get() = contentLowContrast