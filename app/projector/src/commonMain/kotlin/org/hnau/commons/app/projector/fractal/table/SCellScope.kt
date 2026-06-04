package org.hnau.commons.app.projector.fractal.table

import androidx.compose.ui.graphics.Shape

interface SCellScope {

    val corners: STableCorners.Provider

    val shape: Shape
}