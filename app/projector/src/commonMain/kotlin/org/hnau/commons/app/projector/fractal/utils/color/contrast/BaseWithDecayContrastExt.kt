package org.hnau.commons.app.projector.fractal.utils.color.contrast

import org.hnau.commons.app.model.theme.color.Contrast
import org.hnau.commons.app.projector.fractal.utils.BaseWithDecay
import org.hnau.commons.app.projector.fractal.utils.int
import org.hnau.commons.app.projector.fractal.utils.map


fun BaseWithDecay.Companion.contrast(
    initial: Contrast,
    decay: Double,
): BaseWithDecay<Contrast> = BaseWithDecay.int(
    initial = initial.contrast,
    decay = decay,
    baseline = Contrast.min.contrast
).map(::Contrast)



private val containerContrast: BaseWithDecay<Contrast> = BaseWithDecay.contrast(
    initial = Contrast(2),
    decay = 0.75,
)

val Contrast.Companion.container: BaseWithDecay<Contrast>
    get() = containerContrast

private val contentContrast: BaseWithDecay<Contrast> = BaseWithDecay.contrast(
    initial = Contrast(5),
    decay = 0.9,
)

val Contrast.Companion.content: BaseWithDecay<Contrast>
    get() = contentContrast