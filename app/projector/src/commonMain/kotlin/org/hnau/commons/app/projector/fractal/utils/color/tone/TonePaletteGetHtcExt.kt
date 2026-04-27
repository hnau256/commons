package org.hnau.commons.app.projector.fractal.utils.color.tone

import org.hnau.commons.app.model.theme.Tone
import org.hnau.commons.app.model.color.dynamic.hct.Hct
import org.hnau.commons.app.model.color.dynamic.palettes.TonalPalette


fun TonalPalette.getHct(
    tone: Tone,
): Hct = getHct(
    tone = tone.raw,
)