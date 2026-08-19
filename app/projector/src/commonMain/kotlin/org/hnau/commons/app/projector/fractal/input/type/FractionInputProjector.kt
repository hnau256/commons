package org.hnau.commons.app.projector.fractal.input.type

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import arrow.optics.Iso
import org.hnau.commons.app.model.input.InputStateHolder
import org.hnau.commons.app.model.input.InputType
import org.hnau.commons.app.projector.fractal.SProgress
import org.hnau.commons.app.projector.fractal.input.InputContentProjector
import org.hnau.commons.app.projector.fractal.input.InputProjectorPrototype
import org.hnau.commons.app.projector.fractal.input.toInputProjectorPrototype
import org.hnau.commons.app.projector.utils.rememberRun
import org.hnau.commons.kotlin.foldNullable
import org.hnau.commons.kotlin.map


@JvmName("toFractionInputProjectorPrototype")
fun <T : Comparable<T>> InputStateHolder<T, Nothing, InputType.Fraction<T>>.toInputProjectorPrototype(
    floatIso: Iso<T, Float>
): InputProjectorPrototype<T, Nothing, InputType.Fraction<T>> =
    toInputProjectorPrototype { inputType, state, updateState ->
        InputContentProjector.WithoutTitle { itemDrawer ->
            val updateOrNull by updateState.collectAsState()
            val value by state.collectAsState()
            val (range, length) = inputType.range.rememberRun {
                val range = map(floatIso::get)
                val length = (range.endInclusive - range.start).takeIf { it > 0 }
                range to length
            }
            itemDrawer.Item {
                SProgress(
                    getFraction = {
                        val length = length ?: return@SProgress 0f
                        val floatValue = value.let(floatIso::get)
                        (floatValue - range.start) / length
                    },
                    onFractionChanged = updateOrNull?.let { update ->
                        { fraction ->
                            update(
                                length.foldNullable(
                                    ifNull = { inputType.range.start },
                                    ifNotNull = { length ->
                                        val floatValue = fraction * length + range.start
                                        floatIso.set(floatValue)
                                    }
                                )
                            )
                        }
                    }
                )
            }
        }
    }