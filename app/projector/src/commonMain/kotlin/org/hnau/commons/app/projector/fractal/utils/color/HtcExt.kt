package org.hnau.commons.app.projector.fractal.utils.color

import androidx.compose.ui.graphics.Color
import org.hnau.commons.app.model.color.dynamic.hct.Hct

fun Hct.toColor(): Color = Color(toInt())