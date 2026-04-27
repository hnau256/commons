package org.hnau.commons.app.projector.fractal.utils.color.contrast

import org.hnau.commons.app.model.theme.Contrast
import org.hnau.commons.app.projector.fractal.utils.BaseWithDecay
import org.hnau.commons.app.projector.fractal.utils.double
import org.hnau.commons.app.projector.fractal.utils.map


fun BaseWithDecay.Companion.contrast(
    initial: Contrast,
    decay: Double,
): BaseWithDecay<Contrast> = BaseWithDecay.double(
    initial = initial.contrast,
    decay = decay,
    baseline = Contrast.min.contrast
).map(::Contrast)



private val containerContrast: BaseWithDecay<Contrast> = BaseWithDecay.contrast(
    initial = Contrast(2.0),
    decay = 0.5,
)

val Contrast.Companion.container: BaseWithDecay<Contrast>
    get() = containerContrast

private val contentContrast: BaseWithDecay<Contrast> = BaseWithDecay.contrast(
    initial = Contrast(7.0),
    decay = 0.75,
)

val Contrast.Companion.content: BaseWithDecay<Contrast>
    get() = contentContrast