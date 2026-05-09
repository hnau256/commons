package org.hnau.commons.app.projector.fractal.semantic.input.edit

import arrow.core.Either
import org.hnau.commons.app.projector.fractal.semantic.input.SInputMapper
import org.hnau.commons.app.projector.fractal.semantic.input.plus
import org.hnau.commons.app.projector.fractal.semantic.input.simplify

fun <EI, EO, TI, TO> SEditType<EI, TI>.addMapper(
    mapper: SInputMapper<TI, EO, TO>,
): SEditType<Either<EI, EO>, TO> = object : SEditType<Either<EI, EO>, TO> {

    private val source: SEditType<EI, TI>
        get() = this@addMapper
    override val config: SEditType.Config
        get() = source.config

    override val mapper: SInputMapper<String, Either<EI, EO>, TO> = source.mapper + mapper
}

fun <E, T> SEditType<Either<Nothing, E>, T>.simplify(): SEditType<E, T> =
    object : SEditType<E, T> {

        private val source: SEditType<Either<Nothing, E>, T>
            get() = this@simplify
        override val config: SEditType.Config
            get() = source.config

        override val mapper: SInputMapper<String, E, T> =
            source.mapper.simplify()
    }