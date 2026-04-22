package org.hnau.commons.app.projector.fractal.utils.color

import org.hnau.commons.app.projector.fractal.utils.Distance
import org.hnau.commons.app.projector.fractal.utils.Importance

data class DistanceWithImportance(
    val distance: Distance,
    val importance: Importance,
) {

    val distanceByImportance: Distance =
        Distance(distance.distance + importance.importance)
}